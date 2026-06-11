# buildDemoApp.ps1
# Usage: ./buildDemoApp.ps1 -d | -b | -c | -r | -p

$BUNDLETOOL_JAR = "bundletool-all-1.18.3.jar"
$PACKAGE_NAME = "com.adam.app.demoset"
$KEYSTORE_PATH = "SingApk/SignDemo.jks"
$KS_PASS = "123456"
$ALIAS = "SignDemo"
$APKS_PATH = "app.apks"

function Show-Usage {
    Write-Host "Usage: buildDemoApp [option]" -ForegroundColor Yellow
    Write-Host "  -d  Build bundle, generate local testing APKS, and install to device"
    Write-Host "  -b  Build APK"
    Write-Host "  -c  Perform gradlew clean"
    Write-Host "  -r  Perform gradlew clean and build APK"
    Write-Host "  -p  Check device connection and install if confirmed"
    exit 0
}

function Get-BuildType {
    $type = Read-Host "Select Build Type (d: Debug / r: Release) [default: d]"
    if ($type -eq "r") { return "Release" }
    return "Debug"
}

function Install-Process {
    $devices = adb devices | Select-String -Pattern "\tdevice$"
    if ($devices) {
        Write-Host "Device detected. Uninstalling $PACKAGE_NAME..." -ForegroundColor Green
        adb uninstall $PACKAGE_NAME
        Write-Host "Installing split APKs..." -ForegroundColor Green
        if (Test-Path $APKS_PATH) {
            java -jar $BUNDLETOOL_JAR install-apks --apks=$APKS_PATH
        } else {
            Write-Host "Error: $APKS_PATH not found. Please run with -d first." -ForegroundColor Red
        }
    } else {
        Write-Host "No device connected via ADB. Exiting..." -ForegroundColor Red
        exit 0
    }
}

if ($args.Count -eq 0) { Show-Usage }

switch ($args[0]) {
    "-d" {
        $bt = Get-BuildType
        $AAB_PATH = "app/build/outputs/bundle/$($bt.ToLower())/app-$($bt.ToLower()).aab"

        Write-Host "--- [1/3] Building App Bundle ($bt) ---" -ForegroundColor Cyan
        ./gradlew clean ":app:bundle$bt"
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

        Write-Host "--- [2/3] Generating Local Testing APKS ---" -ForegroundColor Cyan
        if (Test-Path $APKS_PATH) { Remove-Item $APKS_PATH }
        java -jar $BUNDLETOOL_JAR build-apks `
            --bundle=$AAB_PATH `
            --output=$APKS_PATH `
            --ks=$KEYSTORE_PATH `
            --ks-pass=pass:$KS_PASS `
            --ks-key-alias=$ALIAS `
            --key-pass=pass:$KS_PASS `
            --local-testing

        if ($LASTEXITCODE -eq 0) {
            Write-Host "--- [3/3] Installing to Device ---" -ForegroundColor Cyan
            Install-Process
        }
    }
    "-b" {
        $bt = Get-BuildType
        Write-Host "--- Building $bt APK ---" -ForegroundColor Cyan
        ./gradlew ":app:assemble$bt"
    }
    "-c" {
        Write-Host "--- Cleaning Project ---" -ForegroundColor Cyan
        ./gradlew clean
    }
    "-r" {
        $bt = Get-BuildType
        Write-Host "--- Cleaning and Building $bt APK ---" -ForegroundColor Cyan
        ./gradlew clean ":app:assemble$bt"
    }
    "-p" {
        $choice = Read-Host "Is the phone connected? (y/n)"
        if ($choice -eq "y") {
            Install-Process
        } else {
            Write-Host "Operation cancelled. Script exiting..." -ForegroundColor Gray
            exit 0
        }
    }
    default { Show-Usage }
}
