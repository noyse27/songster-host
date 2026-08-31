param(
  [string]$OutputDir = "dist"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (-not $env:ANDROID_HOME -and (Test-Path "$env:LOCALAPPDATA\Android\Sdk")) {
  $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
}
if (-not $env:ANDROID_SDK_ROOT -and $env:ANDROID_HOME) {
  $env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
}

if (-not (Test-Path $OutputDir)) {
  New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

if (Test-Path ".\gradlew.bat") {
  .\gradlew.bat assembleDebug assembleRelease
} else {
  gradle assembleDebug assembleRelease
}

$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"
if (-not (Test-Path $debugApk)) {
  throw "APK wurde nicht gefunden: $debugApk"
}

Copy-Item $debugApk "$OutputDir\songster-host-0.1.0-beta-debug.apk" -Force
if (Test-Path $releaseApk) {
  Copy-Item $releaseApk "$OutputDir\songster-host-0.1.0-beta-release.apk" -Force
}
Write-Host "Beta-APK: $OutputDir\songster-host-0.1.0-beta-debug.apk"
if (Test-Path "$OutputDir\songster-host-0.1.0-beta-release.apk") {
  Write-Host "Signed Release-Beta-APK: $OutputDir\songster-host-0.1.0-beta-release.apk"
}
