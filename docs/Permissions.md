# Permissions

This document explains the Android permission model in general,
the specific permissions and roles used by TACL,
and how they are implemented within the app.

## Android Permissions Overview

Android uses a multi-layered security model for permissions:

1. **Install-time Permissions**: Automatically granted by the system when the app is installed.
   These include "Normal" permissions that pose little risk to user privacy.
2. **Runtime Permissions**: Require explicit user approval at runtime. These are "Dangerous"
   permissions that access sensitive data (e.g., Contacts, Location).
3. **Special Permissions & Roles**: High-level access categories that allow apps to perform
   system-level tasks. **Roles** (introduced in Android 10) are a modern way to manage these, where
   the user selects a "Default App" for a specific function.

---

## Permissions and Roles Used by TACL

TACL primarily relies on the **Call Screening Role** to function as an automated call filter.

### Call Screening Role (`ROLE_CALL_SCREENING`)

This is the core "permission" for TACL. By holding this role, TACL becomes a trusted system
component for handling incoming calls.

* **Usage**: It allows the app to receive a callback from the system whenever a call arrives.
* **Details**:
    * No need for the "Dangerous" `READ_PHONE_STATE` or `ANSWER_PHONE_CALLS` permissions for basic
      filtering.
    * Allows the app to silently block calls before the phone even rings.

### Service Binding Permission (`BIND_SCREENING_SERVICE`)

Defined in the `AndroidManifest.xml` for the `TaclService`.

* **Usage**: This is a security requirement. It ensures that **only the Android System** can bind to
  TACL's call screening service.
* **Details**:
    * If a third-party app tries to bind to this service, the system will prevent it because
      the app won't hold this system-level permission.

### Read Phone State (`android.permission.READ_PHONE_STATE`) - *Optional*

The app contains logic that utilize `READ_PHONE_STATE` especially in multi-SIM environments
for identification of the SIM card region.
This is necessary for local or misformed phone numbers to be properly formatted.
Without it may be impossible to provide standardized phone numbers or country for filtering.

* **Usage**: To provide better multi-SIM support by identifying which SIM card is receiving the
  call (via `SubscriptionManager`).
* **Details**:
    * TACL uses `TelephonyManager` and system locale as fallbacks if this permission is missing.

---

## Security and Privacy

* **Privacy**: TACL processes call data locally on the device.
* **Emergency Calls**: TACL is designed to never block emergency calls (verified in
  `TaclService.kt`).
