[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$SourceProjectRoot
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$DestinationProjectRoot = Split-Path -Parent $PSScriptRoot
$ResolvedSource = (Resolve-Path -LiteralPath $SourceProjectRoot).Path
$ResolvedDestination = (Resolve-Path -LiteralPath $DestinationProjectRoot).Path

if ($ResolvedSource -eq $ResolvedDestination) {
    throw 'Source and destination project roots must be different.'
}

$SourceLock = Join-Path $ResolvedSource 'dev\integration-mods.lock.json'
$SourceMods = Join-Path $ResolvedSource 'dev\mods\integration'
$SourceVerifier = Join-Path $ResolvedSource 'tools\setup_integration_mods.ps1'

if (-not (Test-Path -LiteralPath $SourceLock -PathType Leaf)) {
    throw "Source integration lock not found: $SourceLock"
}
if (-not (Test-Path -LiteralPath $SourceMods -PathType Container)) {
    throw "Source integration-mod directory not found: $SourceMods"
}
if (-not (Test-Path -LiteralPath $SourceVerifier -PathType Leaf)) {
    throw "Source setup verifier not found: $SourceVerifier"
}

Write-Host 'Verifying source integration state before migration...'
& $SourceVerifier -VerifyOnly

$DestinationDev = Join-Path $ResolvedDestination 'dev'
$DestinationModsParent = Join-Path $DestinationDev 'mods'
$DestinationMods = Join-Path $DestinationModsParent 'integration'
$DestinationLock = Join-Path $DestinationDev 'integration-mods.lock.json'
$StagingRoot = Join-Path $DestinationDev '.integration-state-restore'

Remove-Item -LiteralPath $StagingRoot -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path (Join-Path $StagingRoot 'mods\integration') | Out-Null

try {
    Copy-Item -LiteralPath $SourceLock -Destination (Join-Path $StagingRoot 'integration-mods.lock.json') -Force
    Get-ChildItem -LiteralPath $SourceMods -File |
        Where-Object { $_.Name -like '*.jar' -or $_.Name -eq '.projectskyblock-managed.json' } |
        Copy-Item -Destination (Join-Path $StagingRoot 'mods\integration') -Force

    New-Item -ItemType Directory -Force -Path $DestinationModsParent | Out-Null
    Remove-Item -LiteralPath $DestinationMods -Recurse -Force -ErrorAction SilentlyContinue
    Move-Item -LiteralPath (Join-Path $StagingRoot 'mods\integration') -Destination $DestinationMods
    Copy-Item -LiteralPath (Join-Path $StagingRoot 'integration-mods.lock.json') -Destination $DestinationLock -Force

    Write-Host 'Verifying migrated integration state...'
    & (Join-Path $PSScriptRoot 'setup_integration_mods.ps1') -VerifyOnly
    Write-Host 'Local integration state restored successfully.'
}
catch {
    Remove-Item -LiteralPath $DestinationMods -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $DestinationLock -Force -ErrorAction SilentlyContinue
    throw
}
finally {
    Remove-Item -LiteralPath $StagingRoot -Recurse -Force -ErrorAction SilentlyContinue
}
