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

$gradleCommand = $null
if (Test-Path ".\gradlew.bat") {
  $gradleCommand = ".\gradlew.bat"
} else {
  $pathGradle = Get-Command gradle -ErrorAction SilentlyContinue
  if ($pathGradle) {
    $gradleCommand = $pathGradle.Source
  } else {
    $cachedGradle = Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists" -Recurse -Filter gradle.bat -ErrorAction SilentlyContinue |
      Sort-Object FullName -Descending |
      Select-Object -First 1
    if ($cachedGradle) {
      $gradleCommand = $cachedGradle.FullName
    }
  }
}

if (-not $gradleCommand) {
  throw "Gradle wurde nicht gefunden. Installiere Gradle oder erzeuge einmalig einen Wrapper mit 'gradle wrapper'."
}

& $gradleCommand assembleDebug assembleRelease

$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"
if (-not (Test-Path $debugApk)) {
  throw "APK wurde nicht gefunden: $debugApk"
}

Copy-Item $debugApk "$OutputDir\adolar-host-0.1.0-beta-debug.apk" -Force
if (Test-Path $releaseApk) {
  Copy-Item $releaseApk "$OutputDir\adolar-host-0.1.0-beta-release.apk" -Force
}
Write-Host "Beta-APK: $OutputDir\adolar-host-0.1.0-beta-debug.apk"
if (Test-Path "$OutputDir\adolar-host-0.1.0-beta-release.apk") {
  Write-Host "Signed Release-Beta-APK: $OutputDir\adolar-host-0.1.0-beta-release.apk"
}
