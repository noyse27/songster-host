param(
  [string]$OutputDir = "dist"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (-not (Test-Path $OutputDir)) {
  New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

if (Test-Path ".\gradlew.bat") {
  .\gradlew.bat assembleDebug
} else {
  gradle assembleDebug
}

$apk = "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apk)) {
  throw "APK wurde nicht gefunden: $apk"
}

Copy-Item $apk "$OutputDir\songster-host-0.1.0-beta-debug.apk" -Force
Write-Host "Beta-APK: $OutputDir\songster-host-0.1.0-beta-debug.apk"
