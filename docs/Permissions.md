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

### Read Phone State (`READ_PHONE_STATE`) - *Optional*

The app contains logic that utilize `READ_PHONE_STATE` especially in multi-SIM environments
for identification of the SIM card region.
This is necessary for local or misformed phone numbers to be properly formatted.
Without it may be impossible to provide standardized phone numbers or country for filtering.

* **Usage**: To provide better multi-SIM support by identifying which SIM card is receiving the
  call (via `SubscriptionManager`).
* **Details**:
    * TACL uses `TelephonyManager` and system locale as fallbacks if this permission is missing.

---

## Background Execution and Potential Blockers

Even when permissions and the call screening role are correctly granted, TACL's ability to screen calls can be affected by system-level restrictions and device-specific behaviors.

### 1. Background Execution & Battery Optimization
* **Unrestricted Battery Usage**:
  Android's doze mode, app standby, and aggressive OEM power management can suspend or terminate background tasks.
  TACL must be allowed to run in the background.
* **Auto-Start & Background Launch**:
  On many manufacturer ROMs, apps require explicit permission in system settings to "Auto-Start" or run background services.
  Without this, the system may prevent TACL from responding to incoming calls.

### 2. Other Potential Blockers
* **OEM Custom ROMs / Aggressive Task Killers**:
  Some manufacturer ROMs aggressively terminate background services or block call screening components.
* **Default Phone App / Dialer Conflicts**:
  Certain default phone apps or OEM dialers may conflict with or override third-party call screening services.
  Ensure TACL remains selected as the system call screening app in system settings.
* **Revoked Roles / Permissions**:
  If permissions or the Call Screening role are revoked by the system or user, the service will stop functioning.

---

## Security and Privacy

* **Privacy**: TACL processes call data locally on the device.
* **Emergency Calls**: TACL is designed to never block emergency calls (verified in
  `TaclService.kt`).
