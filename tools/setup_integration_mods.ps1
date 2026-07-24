[CmdletBinding()]
param(
    [switch]$Refresh,
    [switch]$VerifyOnly
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$ManifestPath = Join-Path $ProjectRoot 'dev\integration-mods.json'
$LockPath = Join-Path $ProjectRoot 'dev\integration-mods.lock.json'
$ModsDirectory = Join-Path $ProjectRoot 'dev\mods\integration'
$ManagedStatePath = Join-Path $ModsDirectory '.projectskyblock-managed.json'
$ApiBase = 'https://api.modrinth.com/v2'
$UserAgent = 'Project-Skyblock-Developer-Validation/1.0 (https://github.com/raziel23x/Project-Skyblock)'

function Read-JsonFile {
    param([Parameter(Mandatory = $true)][string]$Path)
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "Required JSON file not found: $Path"
    }
    return Get-Content -LiteralPath $Path -Raw -Encoding UTF8 | ConvertFrom-Json
}

function Write-Utf8Json {
    param(
        [Parameter(Mandatory = $true)]$Value,
        [Parameter(Mandatory = $true)][string]$Path
    )
    $Json = $Value | ConvertTo-Json -Depth 12
    $Utf8NoBom = [System.Text.UTF8Encoding]::new($false)
    [System.IO.File]::WriteAllText($Path, $Json + [Environment]::NewLine, $Utf8NoBom)
}

function Invoke-ModrinthGet {
    param([Parameter(Mandatory = $true)][string]$RelativePath)
    $Uri = "$ApiBase/$RelativePath"
    return Invoke-RestMethod -Method Get -Uri $Uri -Headers @{ 'User-Agent' = $UserAgent }
}

function ConvertTo-FlatObjectArray {
    param([Parameter(Mandatory = $false)]$Value)

    $Result = [System.Collections.Generic.List[object]]::new()
    if ($null -eq $Value) {
        return @()
    }

    # Windows PowerShell 5.1 may preserve a REST JSON array as one nested
    # System.Object[] pipeline value. Flatten arrays explicitly so each
    # Modrinth version is treated as one record on PowerShell 5.1 and 7+.
    foreach ($Item in @($Value)) {
        if ($Item -is [System.Array]) {
            foreach ($NestedItem in $Item) {
                if ($null -ne $NestedItem) {
                    [void]$Result.Add($NestedItem)
                }
            }
        }
        else {
            [void]$Result.Add($Item)
        }
    }

    return @($Result.ToArray())
}

function Get-DatePublishedSortKey {
    param([Parameter(Mandatory = $true)]$Version)

    $RawDate = $Version.date_published
    if ($RawDate -is [System.Array]) {
        throw "Modrinth version '$($Version.id)' returned multiple date_published values."
    }
    if ([string]::IsNullOrWhiteSpace([string]$RawDate)) {
        throw "Modrinth version '$($Version.id)' did not provide date_published."
    }

    try {
        return [DateTimeOffset]::Parse(
            [string]$RawDate,
            [System.Globalization.CultureInfo]::InvariantCulture
        )
    }
    catch {
        throw "Modrinth version '$($Version.id)' returned invalid date_published '$RawDate'."
    }
}

function Test-VersionCompatibility {
    param(
        [Parameter(Mandatory = $true)]$Version,
        [Parameter(Mandatory = $true)][string]$MinecraftVersion,
        [Parameter(Mandatory = $true)][string]$Loader
    )
    return (@($Version.game_versions) -contains $MinecraftVersion) -and (@($Version.loaders) -contains $Loader)
}

function Select-CompatibleVersion {
    param(
        [Parameter(Mandatory = $true)][string]$ProjectIdOrSlug,
        [Parameter(Mandatory = $true)][string]$MinecraftVersion,
        [Parameter(Mandatory = $true)][string]$Loader,
        [Parameter(Mandatory = $true)][bool]$AllowPrerelease
    )

    $EncodedLoaders = [System.Uri]::EscapeDataString('["' + $Loader + '"]')
    $EncodedVersions = [System.Uri]::EscapeDataString('["' + $MinecraftVersion + '"]')
    $RawVersions = Invoke-ModrinthGet "project/$ProjectIdOrSlug/version?loaders=$EncodedLoaders&game_versions=$EncodedVersions"
    $Versions = @(ConvertTo-FlatObjectArray $RawVersions |
        Where-Object { Test-VersionCompatibility $_ $MinecraftVersion $Loader })

    if ($Versions.Count -eq 0) {
        throw "No $Loader build for Minecraft $MinecraftVersion was found for Modrinth project '$ProjectIdOrSlug'."
    }

    # Always prefer stable releases. A project-specific prerelease allowance only
    # permits beta, then alpha, when no compatible release exists.
    $Preference = if ($AllowPrerelease) { @('release', 'beta', 'alpha') } else { @('release') }
    foreach ($Type in $Preference) {
        $Candidate = $Versions |
            Where-Object { $_.version_type -eq $Type } |
            Sort-Object -Property @{
                Expression = { Get-DatePublishedSortKey $_ }
                Descending = $true
            } |
            Select-Object -First 1
        if ($null -ne $Candidate) {
            return $Candidate
        }
    }

    $AllowedTypes = $Preference -join ', '
    throw "Project '$ProjectIdOrSlug' has compatible builds, but none match the allowed version types: $AllowedTypes."
}

function Resolve-ModrinthGraph {
    param([Parameter(Mandatory = $true)]$Manifest)

    $Selected = @{}
    $Queue = [System.Collections.Generic.Queue[object]]::new()

    foreach ($Project in @($Manifest.projects)) {
        $Queue.Enqueue([PSCustomObject]@{
            project_id_or_slug = [string]$Project.slug
            requested_version_id = $null
            direct = $true
            role = [string]$Project.role
            allow_prerelease = [bool]$Project.allow_prerelease
            requested_by = $null
        })
    }

    while ($Queue.Count -gt 0) {
        $Request = $Queue.Dequeue()
        if ($null -ne $Request.requested_version_id) {
            $Version = Invoke-ModrinthGet "version/$($Request.requested_version_id)"
            if (-not (Test-VersionCompatibility $Version $Manifest.minecraft_version $Manifest.loader)) {
                throw "Required dependency version '$($Version.id)' is incompatible with $($Manifest.loader) $($Manifest.minecraft_version)."
            }
        }
        else {
            $Version = Select-CompatibleVersion `
                -ProjectIdOrSlug $Request.project_id_or_slug `
                -MinecraftVersion $Manifest.minecraft_version `
                -Loader $Manifest.loader `
                -AllowPrerelease $Request.allow_prerelease
        }

        $ProjectId = [string]$Version.project_id
        if ($Selected.ContainsKey($ProjectId)) {
            if ($Selected[$ProjectId].version.id -ne $Version.id -and $null -ne $Request.requested_version_id) {
                throw "Conflicting exact versions were requested for Modrinth project '$ProjectId'."
            }
            continue
        }

        $ProjectMetadata = Invoke-ModrinthGet "project/$ProjectId"
        $Selected[$ProjectId] = [PSCustomObject]@{
            project = $ProjectMetadata
            version = $Version
            direct = [bool]$Request.direct
            role = [string]$Request.role
            requested_by = $Request.requested_by
        }

        foreach ($Dependency in @($Version.dependencies)) {
            if ($Dependency.dependency_type -ne 'required') {
                continue
            }
            if ($null -eq $Dependency.project_id -and $null -eq $Dependency.version_id) {
                throw "Version '$($Version.id)' declares an unsupported required file dependency."
            }
            $Queue.Enqueue([PSCustomObject]@{
                project_id_or_slug = [string]$Dependency.project_id
                requested_version_id = $Dependency.version_id
                direct = $false
                role = "Required dependency of $($ProjectMetadata.title)"
                allow_prerelease = $true
                requested_by = [string]$ProjectId
            })
        }
    }

    $LockedMods = @()
    foreach ($Entry in @($Selected.Values | Sort-Object { $_.project.slug })) {
        $Files = @($Entry.version.files)
        $File = $Files | Where-Object { $_.primary -eq $true } | Select-Object -First 1
        if ($null -eq $File) {
            $File = $Files | Select-Object -First 1
        }
        if ($null -eq $File) {
            throw "Version '$($Entry.version.id)' contains no downloadable file."
        }
        if ([string]::IsNullOrWhiteSpace([string]$File.hashes.sha512)) {
            throw "Version '$($Entry.version.id)' does not provide a SHA-512 hash."
        }

        $LockedMods += [PSCustomObject]@{
            title = [string]$Entry.project.title
            slug = [string]$Entry.project.slug
            project_id = [string]$Entry.project.id
            version_id = [string]$Entry.version.id
            version_number = [string]$Entry.version.version_number
            version_type = [string]$Entry.version.version_type
            file_name = [string]$File.filename
            download_url = [string]$File.url
            sha512 = ([string]$File.hashes.sha512).ToLowerInvariant()
            direct = [bool]$Entry.direct
            role = [string]$Entry.role
            requested_by = $Entry.requested_by
        }
    }

    return [PSCustomObject]@{
        schema = 1
        generated_at_utc = [DateTimeOffset]::UtcNow.ToString('o')
        minecraft_version = [string]$Manifest.minecraft_version
        loader = [string]$Manifest.loader
        source = 'modrinth'
        mods = $LockedMods
    }
}

function Get-Sha512Lower {
    param([Parameter(Mandatory = $true)][string]$Path)
    return (Get-FileHash -LiteralPath $Path -Algorithm SHA512).Hash.ToLowerInvariant()
}

function Install-LockedEnvironment {
    param(
        [Parameter(Mandatory = $true)]$Lock,
        [Parameter(Mandatory = $true)][bool]$OnlyVerify
    )

    New-Item -ItemType Directory -Force -Path $ModsDirectory | Out-Null
    $ExpectedFiles = @($Lock.mods | ForEach-Object { [string]$_.file_name })
    $ExtraJars = @(Get-ChildItem -LiteralPath $ModsDirectory -Filter '*.jar' -File |
        Where-Object { $ExpectedFiles -notcontains $_.Name } |
        Select-Object -ExpandProperty Name)
    if ($ExtraJars.Count -gt 0) {
        throw "Unmanaged JARs are present in the integration directory: $($ExtraJars -join ', ')"
    }

    if ($OnlyVerify) {
        foreach ($Mod in @($Lock.mods)) {
            $Destination = Join-Path $ModsDirectory ([string]$Mod.file_name)
            $Valid = ((Test-Path -LiteralPath $Destination -PathType Leaf) -and ((Get-Sha512Lower $Destination) -eq ([string]$Mod.sha512).ToLowerInvariant()))
            if (-not $Valid) {
                throw "Missing or invalid integration mod: $($Mod.file_name)"
            }
        }
    }
    else {
        $StagingDirectory = Join-Path $ModsDirectory '.projectskyblock-staging'
        Remove-Item -LiteralPath $StagingDirectory -Recurse -Force -ErrorAction SilentlyContinue
        New-Item -ItemType Directory -Force -Path $StagingDirectory | Out-Null

        try {
            foreach ($Mod in @($Lock.mods)) {
                $Destination = Join-Path $ModsDirectory ([string]$Mod.file_name)
                $Valid = ((Test-Path -LiteralPath $Destination -PathType Leaf) -and ((Get-Sha512Lower $Destination) -eq ([string]$Mod.sha512).ToLowerInvariant()))
                if ($Valid) {
                    continue
                }

                $StagedFile = Join-Path $StagingDirectory ([string]$Mod.file_name)
                Write-Host "Downloading $($Mod.title) $($Mod.version_number)..."
                Invoke-WebRequest -UseBasicParsing -Uri $Mod.download_url -OutFile $StagedFile -Headers @{ 'User-Agent' = $UserAgent }
                $ActualHash = Get-Sha512Lower $StagedFile
                if ($ActualHash -ne ([string]$Mod.sha512).ToLowerInvariant()) {
                    throw "SHA-512 mismatch for $($Mod.file_name)."
                }
            }

            foreach ($StagedFile in @(Get-ChildItem -LiteralPath $StagingDirectory -File)) {
                Move-Item -LiteralPath $StagedFile.FullName -Destination (Join-Path $ModsDirectory $StagedFile.Name) -Force
            }

            if (Test-Path -LiteralPath $ManagedStatePath -PathType Leaf) {
                $PreviousState = Read-JsonFile $ManagedStatePath
                foreach ($PreviousFile in @($PreviousState.files)) {
                    if ($ExpectedFiles -notcontains [string]$PreviousFile) {
                        $StalePath = Join-Path $ModsDirectory ([string]$PreviousFile)
                        if (Test-Path -LiteralPath $StalePath -PathType Leaf) {
                            Remove-Item -LiteralPath $StalePath -Force
                            Write-Host "Removed stale managed mod: $PreviousFile"
                        }
                    }
                }
            }

            Write-Utf8Json ([PSCustomObject]@{ schema = 1; files = $ExpectedFiles }) $ManagedStatePath
        }
        finally {
            Remove-Item -LiteralPath $StagingDirectory -Recurse -Force -ErrorAction SilentlyContinue
        }
    }

    foreach ($Mod in @($Lock.mods)) {
        $Destination = Join-Path $ModsDirectory ([string]$Mod.file_name)
        if (-not (Test-Path -LiteralPath $Destination -PathType Leaf)) {
            throw "Integration setup did not produce required file: $($Mod.file_name)"
        }
        if ((Get-Sha512Lower $Destination) -ne ([string]$Mod.sha512).ToLowerInvariant()) {
            throw "Final SHA-512 verification failed for $($Mod.file_name)."
        }
    }

    Write-Host ''
    Write-Host "Integration environment verified for $($Lock.loader) $($Lock.minecraft_version):"
    foreach ($Mod in @($Lock.mods | Sort-Object title)) {
        $Kind = if ($Mod.direct) { 'direct' } else { 'dependency' }
        Write-Host " - $($Mod.title) $($Mod.version_number) [$($Mod.version_type), $Kind]"
    }
}

$Manifest = Read-JsonFile $ManifestPath
if ($Manifest.schema -ne 1) {
    throw "Unsupported integration manifest schema: $($Manifest.schema)"
}

if ($Refresh -or -not (Test-Path -LiteralPath $LockPath -PathType Leaf)) {
    if ($VerifyOnly) {
        throw 'Cannot verify without an existing lock file. Run the setup command first.'
    }
    Write-Host "Resolving compatible Modrinth versions for $($Manifest.loader) $($Manifest.minecraft_version)..."
    $Lock = Resolve-ModrinthGraph $Manifest
    Write-Utf8Json $Lock $LockPath
    Write-Host "Wrote local lock file: $LockPath"
}
else {
    $Lock = Read-JsonFile $LockPath
}

if ($Lock.schema -ne 1 -or $Lock.minecraft_version -ne $Manifest.minecraft_version -or $Lock.loader -ne $Manifest.loader) {
    throw 'The local integration lock does not match the tracked manifest target. Re-run with -Refresh.'
}

$ManifestSlugs = @($Manifest.projects | ForEach-Object { [string]$_.slug } | Sort-Object)
$LockedDirectSlugs = @($Lock.mods | Where-Object { $_.direct -eq $true } | ForEach-Object { [string]$_.slug } | Sort-Object)
if (($ManifestSlugs -join "`n") -ne ($LockedDirectSlugs -join "`n")) {
    throw 'The local integration lock does not match the tracked direct-project set. Re-run with -Refresh.'
}

Install-LockedEnvironment -Lock $Lock -OnlyVerify ([bool]$VerifyOnly)
