[CmdletBinding()]
param(
    [string]$ProjectRoot = ".",
    [string]$Branch = "MC-1.21.1-NeoForge"
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

function Invoke-Git {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments,
        [switch]$Capture
    )

    if ($Capture) {
        $output = & git @Arguments 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "git $($Arguments -join ' ') failed:`n$output"
        }
        return ($output -join "`n").Trim()
    }

    & git @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "git $($Arguments -join ' ') failed with exit code $LASTEXITCODE."
    }
}

$Root = (Resolve-Path -LiteralPath $ProjectRoot).Path
Push-Location $Root

try {
    if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
        throw "Git was not found in PATH. Install GitHub Desktop or Git for Windows first."
    }

    $InsideWorkTree = Invoke-Git -Arguments @("rev-parse", "--is-inside-work-tree") -Capture
    if ($InsideWorkTree -ne "true") {
        throw "The selected folder is not a Git working tree: $Root"
    }

    $CurrentBranch = Invoke-Git -Arguments @("branch", "--show-current") -Capture
    if ($CurrentBranch -ne $Branch) {
        throw "Expected branch '$Branch' but found '$CurrentBranch'."
    }

    $Origin = Invoke-Git -Arguments @("remote", "get-url", "origin") -Capture
    if ($Origin -notmatch "raziel23x/Project-Skyblock(?:\.git)?$") {
        throw "Unexpected origin remote: $Origin"
    }

    Write-Host "Fetching origin/$Branch..."
    Invoke-Git -Arguments @("fetch", "origin", $Branch)

    $LocalHead = Invoke-Git -Arguments @("rev-parse", "HEAD") -Capture
    $RemoteHead = Invoke-Git -Arguments @("rev-parse", "origin/$Branch") -Capture
    $AheadBehind = Invoke-Git -Arguments @(
        "rev-list",
        "--left-right",
        "--count",
        "HEAD...origin/$Branch"
    ) -Capture

    $Counts = $AheadBehind -split "\s+"
    $Ahead = [int]$Counts[0]
    $Behind = [int]$Counts[1]

    if ($Behind -gt 0) {
        throw "Local branch is $Behind commit(s) behind origin/$Branch. Stop and reconcile before committing."
    }

    Write-Host ""
    Write-Host "Current working-tree changes:"
    & git status --short
    if ($LASTEXITCODE -ne 0) {
        throw "git status failed."
    }

    Write-Host ""
    Write-Host "Staging all tracked and new non-ignored source files..."
    Invoke-Git -Arguments @("add", "-A")

    $Staged = Invoke-Git -Arguments @("diff", "--cached", "--name-only") -Capture
    $StagedFiles = @(
        $Staged -split "`n" |
            ForEach-Object { $_.Trim() } |
            Where-Object { $_ -ne "" }
    )

    if ($StagedFiles.Count -eq 0) {
        Write-Host "No source changes are staged. Nothing to commit."
        exit 0
    }

    $ForbiddenPatterns = @(
        '(^|/)\.gradle(/|$)',
        '(^|/)build(/|$)',
        '(^|/)logs(/|$)',
        '(^|/)crash-reports(/|$)',
        '(^|/)run-clean(/|$)',
        '(^|/)run-standalone(/|$)',
        '(^|/)run-integration(/|$)',
        '^dev/mods/',
        '^dev/integration-mods\.lock\.json$',
        '\.jar$',
        'session\.lock$'
    )

    $Forbidden = @()
    foreach ($File in $StagedFiles) {
        foreach ($Pattern in $ForbiddenPatterns) {
            if ($File -match $Pattern) {
                $Forbidden += $File
                break
            }
        }
    }

    if ($Forbidden.Count -gt 0) {
        & git reset
        throw (
            "Refusing to commit forbidden local/generated files:`n - " +
            (($Forbidden | Sort-Object -Unique) -join "`n - ")
        )
    }

    Write-Host ""
    Write-Host "Files staged for Milestone 18:"
    $StagedFiles | Sort-Object | ForEach-Object { Write-Host " - $_" }

    $CommitTitle = "Milestone 18: establish tiered developer validation environments"
    $CommitBody = @"
- add isolated standalone and integration NeoForge client profiles
- resolve, lock, hash, restore, and validate optional helper mods
- keep helper JARs out of compilation, standalone runtime, and release artifacts
- make integration launch depend on native Gradle environment validation
- restore the Repair Gem Curios slot through optional data-driven resources
- document validation contracts, migration findings, and regression coverage

Validated on Windows with NeoForge 21.1.235 and Minecraft 1.21.1.
No gameplay balance, save format, or required runtime dependency changes.
"@

    Write-Host ""
    Write-Host "Creating commit..."
    Invoke-Git -Arguments @(
        "commit",
        "-m", $CommitTitle,
        "-m", $CommitBody
    )

    $NewHead = Invoke-Git -Arguments @("rev-parse", "HEAD") -Capture

    Write-Host ""
    Write-Host "Pushing $Branch..."
    Invoke-Git -Arguments @("push", "origin", $Branch)

    Write-Host ""
    Write-Host "Milestone 18 pushed successfully."
    Write-Host "Commit: $NewHead"
    Write-Host "Branch: $Branch"
    Write-Host "Origin: $Origin"
}
catch {
    Write-Error $_
    exit 1
}
finally {
    Pop-Location
}
