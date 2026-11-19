# Google Play Data Safety Responses

**App:** BAC Calculator (`letshangllc.baccalculator`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

Use this document when filling out the Google Play Console Data safety form.

---

## Overview

This app calculates an estimated blood alcohol content from user-entered values (weight, drinks, time, gender). It does **not** create accounts or sync personal data to a developer backend. It displays ads via Google AdMob and gathers GDPR/consent preferences via Google UMP.

Calculations run on-device. Drink/weight inputs are not stored by the app beyond the current session (except a local flag that the user accepted the legal disclaimer).

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| App interactions | Yes | Yes (AdMob) | Advertising |
| Advertising ID / Device or other IDs | Yes | Yes (AdMob) | Advertising |
| Approximate location | No (permission removed) | — | — |
| Personal info (name, email, etc.) | No | — | — |
| Health info | No* | — | — |

\*Users enter weight and drink counts for an on-device estimate. These values are not transmitted by the app itself; treat them as ephemeral app functionality inputs unless AdMob/UMP incidentally observe UI context (they should not receive form field contents).

---

## Third-Party SDKs

### Google Mobile Ads (AdMob)
- **Data collected:** Advertising ID, device info, app interactions, IP (by Google)
- **Purpose:** Display advertisements
- **Shared:** Yes, with Google advertising partners
- **User control:** UMP consent for personalized ads (EEA/UK); device ad settings
- **Privacy policy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR/consent management before ads are requested
- **Data collected:** Consent preferences
- **Privacy policy:** https://policies.google.com/privacy

---

## Permissions Declared

| Permission | Why |
|------------|-----|
| `INTERNET` | Load ads / consent forms over HTTPS |
| `ACCESS_NETWORK_STATE` | AdMob connectivity checks |

**Removed vs old listing (do not declare as used by the app):**
- `ACCESS_COARSE_LOCATION` — removed; not needed
- `WRITE_EXTERNAL_STORAGE` — removed; not needed

No notification permission is requested.

---

## Data Security

- **Encrypted in transit:** Yes — AdMob/UMP traffic uses HTTPS.
- **Data deletion:** No developer-held account data. Users can clear app storage / uninstall. Advertising ID can be reset in system settings.

---

## Children's Privacy / Target Audience

This is an **alcohol-related** app.

- Target audience in Play Console must be set to **18+** (and/or **21+** where applicable). **Do not** include children or “Families” programs.
- In-app disclaimer states results are **estimates only** and must **not** be used to decide whether to drive.
- UMP is configured with `setTagForUnderAgeOfConsent(false)` because the app is not directed at children. Do **not** enable child-directed ad treatment for this title.

---

## Play Console Form Responses (suggested)

### Does your app collect or share any of the required user data types?
**Yes**

### Is all of the user data collected by your app encrypted in transit?
**Yes**

### Do you provide a way for users to request that their data is deleted?
**Yes** (uninstall / clear storage; no developer account store)

### Data types to declare

1. **App interactions**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising
2. **Device or other IDs** (Advertising ID)
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising

### Ads
- App contains ads: **Yes**
- Uses AdMob with UMP consent

---

## Contact

For privacy inquiries, use the developer email on the Google Play Store listing.
