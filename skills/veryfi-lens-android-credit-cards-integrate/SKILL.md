---
name: veryfi-lens-android-credit-cards-integrate
description: "Integrates the Veryfi Lens Android Credit Cards UI SDK (com.veryfi.lens:veryfi-lens-credit-cards-sdk:2.7.0.6): Maven setup, on-device VeryfiInApp reading, VeryfiLens.configure, card VeryfiLensSettings, delegate card JSON, and showCamera. Use when adding Lens credit-card capture with the Lens camera UI on Android. For an app-owned camera use the separate Headless Credit Cards coordinate com.veryfi.lens.headless:credit-cards-sdk."
---

# Veryfi Lens Android — Credit Cards

Walk a host Android app from Gradle to a working first scan. Change the fewest flags. Do not invent SDK APIs. Facts were read from the Android SDK at version `2.7.0.6` (commit `7f33726c`). Anything not confirmed there is marked **Unconfirmed**.

**Coordinate:** `com.veryfi.lens:veryfi-lens-credit-cards-sdk:2.7.0.6`  
**Gradle flavor:** `lensCreditCards`  
**Allowed document types:** `DocumentType.CREDIT_CARD` only.

Public entry point: `com.veryfi.lens.VeryfiLens` with `com.veryfi.lens.VeryfiLensDelegate`. Settings type: `com.veryfi.lens.helpers.VeryfiLensSettings`.

This folder is complete by itself. Duplicate guidance across products is intentional.

## Pick a workflow

- **Quick** — the host already asked for this product. Ask nothing else. Apply the settings in [settings.md](settings.md), wire [implementation.md](implementation.md), and state the assumptions below.
- **Guided** — something material is missing. Ask only the unknown questions (at most four, one message), then continue.

### Quick workflow

```
veryfi-lens-android-credit-cards-integrate quick:
- [ ] Confirm this artifact (install.md)
- [ ] Add the Maven repository and this one dependency
- [ ] Permissions (install.md)
- [ ] Credentials + settings from settings.md
- [ ] setDelegate, then configure { ok -> }, then showCamera() only when ok is true
- [ ] Handle the callbacks in callbacks.md
- [ ] Run the conflicts in troubleshooting.md
```

Assumptions when the host was specific about the product and silent on the rest: Lens camera UI, `CREDIT_CARD`, `VeryfiInApp`, gallery off, auto-capture on, hybrid crop layout on.

### Guided workflow

1. **Camera owner** — Lens camera UI (this artifact) or the host's camera (Headless coordinate `com.veryfi.lens.headless:credit-cards-sdk`)?
2. **Fields** — number, name, date, and CVC, or a subset?
3. **Speed** — `CreditCardAutoCaptureMode` Speed, Normal, or Accuracy?
4. **Privacy** — confirm card images must stay off the Cloud image-upload path (this recipe does).

Defaults: Lens UI, all four detectors, mode Normal.


## Rules

1. One dependency: `com.veryfi.lens:veryfi-lens-credit-cards-sdk:2.7.0.6`. `documentTypes[0]` must be `CREDIT_CARD`.
2. `checkCreditCardSettings()` forces `dataExtractionEngine = VeryfiInApp` and `autoLightDetectionIsOn = false` when the first type is `CREDIT_CARD`.
3. `CaptureFragmentUIManager` sets `autoCaptureIsOn = true` when the selected type is `CREDIT_CARD`.
4. When stored prefs load (`moreMenuIsOn` and a previous session), `galleryIsOn` is forced false for credit cards. Leave gallery off.
5. Card images do not take the Cloud API upload branch (`notifyServerBeforeUpload` / `veryfiCloudApi`). The InApp branch calls `CardExtraction.getCardData` and broadcasts that JSON to the delegate. A doc-ready metadata POST still runs (`notifyServerOfDocumentReady`: engine name, `sdk` = `android`, document information such as type, corners, image size, LCD scores). It does not send the PAN.
6. `VeryfiLens.uploadImages` is the UI file pipeline. It is not how this product reads a card, and it is not the Headless Credit Cards SDK.


## Do not

- Copy a repository password, keystore, or credential from the SDK repo or the Demo app. Maven access comes from Veryfi. Keep it in `~/.gradle/gradle.properties` or CI secrets.
- Depend on a second Lens UI flavor, or on `com.veryfi.lens.core:veryfi-lens-core-sdk` (email and obsolete dictation; adds `WRITE_CONTACTS` and `RECORD_AUDIO`).
- Treat Demo types as the client contract: `SettingsHolder`, Demo analyzers, `ImageProxyUtils`, `VeryfiLensHelper`.
- Set `dictateIsOn = true` or call `VeryfiLensCore.showDictation()`.
- Set `anyDocumentType` (deprecated local regex OCR). A new template id is `anyDocumentTemplate`, and only after Veryfi support confirms it.
- Set `useLegacyModels` (default true; no partner contract).
- Quote AAR size as the installed app size. Compare the final app with the SDK linked and removed. [SDK binary sizes](https://docs.veryfi.com/lens/mobile/settings/sdk_binary_sizes/).
- Treat `allowSubmittingScreenshots` (rejects imported screenshots) and `enableScreenshots` (device screenshots of Lens screens, default true) as one switch.
- Set `VeryfiCloudAPI` and expect it to stick. Configure overwrites it.
- Enable `galleryIsOn` for card photos.
- Send card images through `uploadImages` as a cloud extraction path.
- Use Full, Remote Models, or Lite for `CREDIT_CARD`.
- Mutate `VeryfiLensHelper` from the host.


## What to hand back

1. The credit-cards UI dependency only.
2. `creditCardSettings()` with `VeryfiInApp` and gallery off.
3. Delegate that reads card JSON from `data`.
4. A privacy note: on-device read, metadata doc-ready, no Cloud image upload.
5. If the host owns the camera, stop and name `com.veryfi.lens.headless:credit-cards-sdk:2.7.0.6` without mixing the two configures.


## Files in this folder

- [install.md](install.md) — Maven coordinate, requirements, permissions, app size
- [implementation.md](implementation.md) — configure, delegate, and launch or camera lifecycle
- [settings.md](settings.md) — `VeryfiLensSettings` for this product, with Android defaults
- [callbacks.md](callbacks.md) — delegate payloads
- [troubleshooting.md](troubleshooting.md) — symptom, cause, fix
- [clarifications.md](clarifications.md) — Unconfirmed items, Any-Documents, fraud, field detection
- [checklist.md](checklist.md) — validation checklist before handing code to the host

