# TACL

***Telephone Access Control List (TACL)***

[![Latest Release](https://codeberg.org/brunothg/TACL/badges/release.svg)](https://codeberg.org/brunothg/TACL/releases)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Reproducable Build](https://shields.rbtlog.dev/simple/alt.foss.brunothg.tacl)](https://shields.rbtlog.dev/alt.foss.brunothg.tacl)
[![IzzyOnDroid](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/alt.foss.brunothg.tacl&label=IzzyOnDroid)](https://apt.izzysoft.de/packages/alt.foss.brunothg.tacl)
[![Obtainium](https://img.shields.io/badge/Obtainium-Compatible-brightgreen)](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://codeberg.org/brunothg/TACL)

TACL is a powerful call filtering app for Android power users. It allows you to define complex rules
to intercept and manage incoming calls, ensuring you only hear from the people you want.

<div style="display:flex; justify-content:center; align-items:center;">
  <img alt="App Icon" src="fastlane/metadata/android/en-US/images/icon.png" width="128" />
</div>

> [!IMPORTANT]
> **Important Compatibility Note:**  
> TACL is based on Android's `CallScreeningService` and will not work if blocked by the phone manufacturer, custom ROM, or certain default phone apps.
> It is also required that the necessary system roles and permissions are granted — see the [Permissions](docs/Permissions.md) guide for details.

> [!NOTE]
> **Testing Scope:**  
> The app is mainly tested against Android Virtual Devices (AVD) and a small selection of available real-life phones.
> For issues caused by a specific device or ROM, no support can be given.

## Key Features

- **Regex Matching**: Match incoming numbers using powerful regular expressions.
- **Custom Actions**: Choose whether to ignore, hang up, silence, or hide calls from your log.
- **Priority-based Filtering**: Rules are applied in a specific order, allowing for fine-grained control.
- **Privacy First**: No internet access, no tracking, all data stays on your device. For more details about permissions and their usage see [Permissions](docs/Permissions.md)

## Getting Started

For detailed instructions on how to install TACL, please see
the [Installation Guide](docs/InstallationGuide.md).

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroidButtonGreyBorder_nofont.png"
alt="Get it on IzzyOnDroid"
height="60">](https://apt.izzysoft.de/packages/alt.foss.brunothg.tacl)

[<img src="https://github.com/ImranR98/Obtainium/blob/main/assets/graphics/badge_obtainium.png?raw=true"
alt="Get it on Obtainium"
height="80">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://codeberg.org/brunothg/TACL)

[<img src="https://get-it-on.codeberg.org/get-it-on-blue-on-white.png"
alt="Get it on Codeberg"
height="60">](https://codeberg.org/brunothg/TACL/releases)

### Basic Usage

1. Open TACL and grant the required **Call Screening** role.
2. Click the **+** button to create a new filter.
3. Define a **Matcher** (e.g., a regex for a specific area code).
4. Define an **Action** (e.g., Block + Hangup).
5. Save and ensure the filter is enabled.

For more details on what each action does, see [Call Filter Actions](docs/FilterActions.md).

## Screenshots

| <img alt="Main screen" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_main_screen.png" width="512"> | <img alt="Editor screen" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_editor_screen.png" width="512"> | <img alt="Editor screen" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_log_screen.png" width="512"> |
|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|

## License

This project is licensed under GPLv3 (see [LICENSE](./LICENSE)).  
For used third party licenses see [THIRD_PARTY_LICENSES.md](./THIRD_PARTY_LICENSES.md).
