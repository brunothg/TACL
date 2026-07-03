# Installation Guide

This guide explains how to install TACL on your Android device using different methods.

## Prerequisites
- Android 10 (API 29) or higher.
- For some features (like advanced call log suppression), the app may need to be installed as a **System App**.

## Verification Info
- Application ID: `alt.foss.brunothg.tacl`
- SHA-256 fingerprint: `8da0de10ea4b5baa578b2e0fe3a64f821214b233ccec653c458a05aa42b66034`

## Method 1: Obtainium
Obtainium allows you to install and update Open Source apps directly from their release pages (Codeberg, GitHub, GitLab, etc.).

1. Install [Obtainium](https://github.com/ImranR98/Obtainium).
2. Add the App Source: `https://codeberg.org/brunothg/TACL`
3. Click **Install**.

## Method 2: ADB (Manual Installation)
If you are a power user and want to install the APK manually using Android Debug Bridge (ADB).

1. Download the latest APK from the [Releases page](https://codeberg.org/brunothg/TACL/releases).
2. Connect your phone to your computer and enable **USB Debugging**.
3. Run the following command:
   ```bash
   adb install tacl-release.apk
   ```

### Installing as a System App (Advanced)
To suppress the call log entry for blocked calls on some Android versions, TACL must be installed as a system app.
1. Root access is required.
2. Move the APK to `/system/priv-app/TACL/TACL.apk`.
3. Set permissions to `0644`.
4. Reboot your device.

