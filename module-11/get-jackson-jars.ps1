# Name:       Rakesh Shrestha
# Assignment: CSD420 Module 11.2 Programming Assignment
# Date:       November 22, 2026
# Purpose:    Downloads the three core Jackson 2.21.8 (LTS) JAR files from Maven
#             Central into the lib folder, then zips all three into one file:
#             lib\jackson-2.21.8-all-jars.zip
#
# Run from the folder that holds this script:
#   powershell -ExecutionPolicy Bypass -File .\get-jackson-jars.ps1

$ErrorActionPreference = "Stop"
$lib = Join-Path $PSScriptRoot "lib"
New-Item -ItemType Directory -Force -Path $lib | Out-Null

$base = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core"
$jars = @(
    "$base/jackson-core/2.21.8/jackson-core-2.21.8.jar",
    "$base/jackson-databind/2.21.8/jackson-databind-2.21.8.jar",
    "$base/jackson-annotations/2.21/jackson-annotations-2.21.jar"
)

foreach ($url in $jars) {
    $name = Split-Path $url -Leaf
    Write-Host "Downloading $name"
    Invoke-WebRequest -Uri $url -OutFile (Join-Path $lib $name)
}

$zip = Join-Path $lib "jackson-2.21.8-all-jars.zip"
if (Test-Path $zip) { Remove-Item $zip }
Compress-Archive -Path (Join-Path $lib "*.jar") -DestinationPath $zip
Write-Host "Done. All JAR files zipped together in $zip"
