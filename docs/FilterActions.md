# Call Filter Actions

When a call matches a filter, TACL can perform various actions. This page explains how these actions work and their technical limitations.

## Action Types

### 1. Block
Decides if the call should be allowed to reach you.
- **Ignore**: The call is blocked, but the phone continues to "ring" for the caller until they hang up or it goes to voicemail. Your phone stays silent.
- **Hangup**: The call is immediately terminated. The caller will hear a busy signal or be disconnected.

> [!IMPORTANT]
> **Blocking is a prerequisite** for suppressing notifications and call log entries. If you don't block the call, the system default behavior for notifications and logs will apply.

### 2. Ring
Controls whether your phone should play a ringtone for the incoming call.
- If disabled, the call will be silenced even if it's not blocked.

### 3. Notification
Controls whether a missed call notification should be shown after a call is blocked.
- **Note**: Only works if **Block** is also active.

### 4. Call Log
Controls whether the call should appear in your device's call log.
- **Note**: Only works if **Block** is also active.

## Technical Specialties & Limitations

### System App Requirement
On many Android versions, the system prevents third-party apps from removing entries from the call log for security reasons. To bypass this, TACL must be installed as a **System App**. Without this, the "Call Log" setting may be ignored by the operating system.

### Android Version Differences
- **Android 10+**: Uses `CallScreeningService`, which allows for basic filtering without special permissions.
- **Call Suppression**: The reliability of `setSkipCallLog` and `setSkipNotification` varies by manufacturer and Android version. Some "clean" Android builds (like Pixel) respect these settings better than heavily modified ones.
- **Known callers**: Calls that are known (contacts from the phonebook) most likely won't be blocked or filtered at all.

### Emergency Calls
TACL **never** filters emergency calls. These are always allowed through to ensure your safety.
