#!/bin/bash
# buildDemoApp.sh
# Usage: ./buildDemoApp.sh -d | -b | -c | -r | -p

BUNDLETOOL_JAR="bundletool-all-1.18.3.jar"
AAB_PATH="app/build/outputs/bundle/debug/app-debug.aab"
APKS_PATH="app.apks"
PACKAGE_NAME="com.adam.app.demoset"
KEYSTORE_PATH="SingApk/SignDemo.jks"
KS_PASS="123456"
ALIAS="SignDemo"

show_usage() {
    echo "Usage: buildDemoApp [option]"
    echo "  -d  Build bundle, generate local testing APKS, and install to device"
    echo "  -b  Build debug APK"
    echo "  -c  Perform gradlew clean"
    echo "  -r  Perform gradlew clean and build debug APK"
    echo "  -p  Check device connection and install if confirmed"
    exit 0
}

install_process() {
    devices=$(adb devices | grep -E "\tdevice$")
    if [ ! -z "$devices" ]; then
        echo -e "\033[0;32mDevice detected. Uninstalling $PACKAGE_NAME...\033[0m"
        adb uninstall $PACKAGE_NAME
        echo -e "\033[0;32mInstalling split APKs...\033[0m"
        if [ -f "$APKS_PATH" ]; then
            java -jar $BUNDLETOOL_JAR install-apks --apks=$APKS_PATH
        else
            echo -e "\033[0;31mError: $APKS_PATH not found. Please run with -d first.\033[0m"
        fi
    else
        echo -e "\033[0;31mNo device connected via ADB. Exiting...\033[0m"
        exit 0
    fi
}

if [ $# -eq 0 ]; then
    show_usage
fi

case "$1" in
    -d)
        echo -e "\033[0;36m--- [1/3] Building App Bundle (AAB) ---\033[0m"
        ./gradlew :app:bundleDebug
        if [ $? -ne 0 ]; then exit $?; fi

        echo -e "\033[0;36m--- [2/3] Generating Local Testing APKS ---\033[0m"
        rm -f $APKS_PATH
        java -jar $BUNDLETOOL_JAR build-apks \
            --bundle=$AAB_PATH \
            --output=$APKS_PATH \
            --ks=$KEYSTORE_PATH \
            --ks-pass=pass:$KS_PASS \
            --ks-key-alias=$ALIAS \
            --key-pass=pass:$KS_PASS \
            --local-testing

        if [ $? -eq 0 ]; then
            echo -e "\033[0;36m--- [3/3] Installing to Device ---\033[0m"
            install_process
        fi
        ;;
    -b)
        echo -e "\033[0;36m--- Building Debug APK ---\033[0m"
        ./gradlew :app:assembleDebug
        ;;
    -c)
        echo -e "\033[0;36m--- Cleaning Project ---\033[0m"
        ./gradlew clean
        ;;
    -r)
        echo -e "\033[0;36m--- Cleaning and Building Debug APK ---\033[0m"
        ./gradlew clean :app:assembleDebug
        ;;
    -p)
        read -p "Is the phone connected? (y/n): " answer
        if [ "$answer" == "y" ]; then
            install_process
        else
            echo "Operation cancelled. Script exiting..."
            exit 0
        fi
        ;;
    *)
        show_usage
        ;;
esac
