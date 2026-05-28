# MIT License Header (Implicit)
# Automation script for Dynamic Delivery Local Testing

$BUNDLETOOL_JAR = "bundletool-all-1.18.3.jar"
$AAB_PATH = "app/build/outputs/bundle/debug/app-debug.aab"
$APKS_PATH = "app.apks"
$PACKAGE_NAME = "com.adam.app.demoset"
$KEYSTORE_PATH = "SingApk/SignDemo.jks"
$KS_PASS = "123456"
$ALIAS = "SignDemo"

Write-Host "--- [1/4] Building App Bundle (AAB) ---" -ForegroundColor Cyan
./gradlew :app:bundleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle build failed."
    exit $LASTEXITCODE
}

Write-Host "--- [2/4] Generating Local Testing APKS ---" -ForegroundColor Cyan
if (Test-Path $APKS_PATH) { Remove-Item $APKS_PATH }

java -jar $BUNDLETOOL_JAR build-apks `
    --bundle=$AAB_PATH `
    --output=$APKS_PATH `
    --ks=$KEYSTORE_PATH `
    --ks-pass=pass:$KS_PASS `
    --ks-key-alias=$ALIAS `
    --key-pass=pass:$KS_PASS `
    --local-testing

if ($LASTEXITCODE -ne 0) {
    Write-Error "Bundletool failed to generate APKS."
    exit $LASTEXITCODE
}

Write-Host "--- [3/4] Checking Device Connection ---" -ForegroundColor Cyan
while ($true) {
    $devices = adb devices | Select-String -Pattern "\tdevice$"
    if ($devices) {
        Write-Host "Device detected: $(($devices[0].ToString()).Split("`t")[0])" -ForegroundColor Green
        break
    }

    Write-Host "No device connected via ADB. Please connect your phone and ensure USB debugging is enabled." -ForegroundColor Red
    $choice = Read-Host "Do you want to retry checking for the device? (y/n)"
    if ($choice -notmatch "^y$") {
        Write-Host "Operation cancelled. Script exiting..." -ForegroundColor Gray
        exit 0
    }
}

Write-Host "--- [4/4] Preparing Device & Final Confirmation ---" -ForegroundColor Yellow
Write-Host "Uninstalling $PACKAGE_NAME to ensure clean local-testing state..."
adb uninstall $PACKAGE_NAME

$confirmation = Read-Host "Do you want to install the APKS to the connected device now? (y/n)"
if ($confirmation -match "^y$") {
    Write-Host "Installing APKS..." -ForegroundColor Green
    java -jar $BUNDLETOOL_JAR install-apks --apks=$APKS_PATH
    Write-Host "Success! You can now open the App and test Dynamic Delivery." -ForegroundColor Green
} else {
    Write-Host "Installation cancelled. The APKS file is saved at $APKS_PATH" -ForegroundColor Gray
}
