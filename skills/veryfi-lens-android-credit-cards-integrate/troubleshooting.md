# Veryfi Lens Android — Credit Cards — troubleshooting

Source of truth: Veryfi Lens Android SDK **2.7.0.6**, commit `7f33726c`. Names below were read from that tree. Items not fully confirmed are marked **Unconfirmed**. Do not copy repository passwords, keystores, or Demo-only types into the host app.


Lens UI logs go through `LogHelper` (logcat). Error callbacks also upload SDK logs.

## Build and launch

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Could not find com.veryfi.lens:veryfi-lens-credit-cards-sdk` | Nexus repository missing or no access | Add `https://nexus.veryfi.com/repository/maven-releases/`. Ask Veryfi for access. Credentials from Gradle properties |
| Duplicate class or resource merge | Two Lens UI artifacts, or Core plus a UI artifact | Keep this one dependency |
| Manifest provider authority clash | Host redeclares `${applicationId}.veryfi.lens.fileprovider` | Remove the host duplicate |
| `Manifest merger failed` on `application@allowBackup` | New-app template sets `android:allowBackup="true"`; Lens libraries set `false` | On `<application>`, set `android:allowBackup="false"` and `tools:replace="android:allowBackup"` (`xmlns:tools` on `<manifest>`). Append to an existing `tools:replace` list. Keep `true` only if the host must enable Auto Backup |
| `configure` callback false | No network on first launch with no cached region, or credentials rejected | Retry online. Check `clientId`, `username`, `apiKey` |
| Toast `The URL [..] is not valid.` | Bad `credentials.url` | Remove it or use the URL Veryfi issued |
| Settings edits ignored for returning users | `moreMenuIsOn` true loads stored prefs over the host | Set `moreMenuIsOn = false` |
| Settings differ from code on some accounts | Dashboard `lens_settings` merged | `ignoreRemoteSettings = true` |
| Toast `Please use only valid document types, refer to hub docs: Lens for Credit Cards` | `documentTypes` is outside this flavor | Match the allow-list in install.md |
| Toast `Please specify the document types` | `documentTypes` is null | Set the list |
| `showCamera()` does nothing and no callback | Called before configure, or debounced | Present inside `configure { ok -> }` |
| Toast `Device Nexus 5X not supported` | SDK device block | Unsupported device |


## This product

| Symptom | Cause | Fix |
|---------|-------|-----|
| Cloud engine ignored | `checkCreditCardSettings` | Expected. Use `VeryfiInApp` |
| Gallery button missing | Forced off for credit cards when prefs load | Expected |
| `data` missing card fields | Parsed `CardExtraction` JSON; log one payload | Read `card_number`, `card_name`, `card_dates`, `card_cvc`, `card_uuid` |
| Host wanted its own preview | This artifact starts `CaptureActivity` | Headless artifact `com.veryfi.lens.headless:credit-cards-sdk` |


## Conflicts

Prefer deleting an extra flag over adding another one.

| Combination | What happens | Fix |
|-------------|--------------|-----|
| `CREDIT_CARD` on Full, Remote Models, Field Detection, or Lite | Flavor toast; camera closed | This credit-cards artifact |
| `VeryfiCloudAPI` left as the engine | `checkCreditCardSettings` forces `VeryfiInApp` | Set `VeryfiInApp` in code too |
| `galleryIsOn = true` | Forced false when stored prefs load | Leave false |
| `autoCaptureIsOn = false` | Forced true on the card camera | Leave true |
| `cropLayoutIsOn` without `cropLayoutWithAutoDetection` while expecting a green box | ML crop uses the frame only when both are true | Set both for hybrid |
| `uploadImages` used as the card API | Different pipeline | `showCamera()` |
| `fraudDetectionIsOn` without `detect_fraud_on` | Models may be requested, runtime fraud stays off | Account owner enables the backend flag |


| `ignoreRemoteSettings` left false | Dashboard overwrites the object | true |
| `backupDocsToGallery` left true | Scans land in the gallery; storage prompt on API < 33 | false, and More menu off so the user cannot turn it back on |
| `moreMenuIsOn` true on a returning user | Stored prefs replace host capture flags | false |
| `glareDetectionIsOn` true (Android default) and `allowSubmitUndetectedDocsIsOn` false | Glare dialog has no OK path | Glare off, or allow undetected |
| `blurDetectionIsOn` true, undetected disallowed, `allowSubmitBlurredDocsIsOn` false | Blur dialog cannot continue | Torch, `tryAgainLightIsOn`, or allow blurred |
| `allowSubmitUndetectedDocsIsOn` true while blur is meant to block | The undetected flag also lets blur and glare continue | Set both false to block |
| `sameFileUploadPreventionPolicy = Always` without `VeryfiCloudAPI` | Hashes are not persisted | `Session`, or Cloud engine |
| `None` plus `uploadWithoutProcessingIsOn = false` plus auto-delete true | No retained `local_package_path` | `autoDeleteLocalResourcesAfterProcessing = false` |
| `uploadWithoutProcessingIsOn = true` without `None` | Flag ignored | Set `ExtractionEngine.None` |
