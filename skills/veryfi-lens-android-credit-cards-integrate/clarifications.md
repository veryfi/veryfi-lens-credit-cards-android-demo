# Veryfi Lens Android — Credit Cards — clarifications

Source of truth: Veryfi Lens Android SDK **2.7.0.6**, commit `7f33726c`. Names below were read from that tree. Items not fully confirmed are marked **Unconfirmed**. Do not copy repository passwords, keystores, or Demo-only types into the host app.


## Any-Documents

`checkLensCreditCards` allows `CREDIT_CARD` only. Any-Documents templates and ID shortcuts are rejected. Escalate those to Veryfi support and a different artifact.

Nutrition facts, passport, driver license, shipping label, insurance card, and prescription label are Any-Documents shortcuts, not separate products. A new `anyDocumentTemplate` string comes from Veryfi support. `anyDocumentType` is the deprecated local-regex enum (`INE`, `Passport`, `ColombianId`, `DriverLicense`, `HealthCare`). Leave it null and set `anyDocumentTemplate` when support has confirmed a template id. `cameraProcessingMode` (`Document` or `Card`) is documented as valid for `ANY_DOCUMENT` only.


## Fraud detection (paid, backend-gated)

`fraudDetectionIsOn` defaults to **false**. `ImageProcessor.isFraudDetectionOn` is true only when that flag is true **and** `Preferences.isFraudDetectionOn` was set from the validate-client field `detect_fraud_on` (`PartnerHelper`). Setting the flag in the host does not turn fraud on by itself. Ask the account owner to enable `detect_fraud_on`.

`getRequiredModels` adds `LcdDetector`, `LcdCardDetector`, and `ObjectDetector` when `fraudDetectionIsOn` is true. `showLCDIsNotAllowed` (default false) shows the LCD dialog when a score is over 0.5.

`getRequiredModels` adds LCD/object models when `fraudDetectionIsOn` is true, and the card detector receives `isFraudDetectionOn` only when that flag and `Preferences.isFraudDetectionOn` (`detect_fraud_on`) are both true. Do not promise fraud results from the setting alone. Card reading itself does not require fraud.


## Local field detection (paid, flavor-gated)

`documentFieldsToDetect` accepts `VeryfiLensSettings.DocumentFields`: `Vendor`, `Date`, `Time`, `Total`, `Tax`. `getRequiredModels` adds `FieldDetection` and `WordSPM` only for `RECEIPT`, `LONG_RECEIPT`, `INVOICE`, and `OTHER`.

The native field-detection build (`-DVERYFI_FIELD_DETECTION=ON` and source set `fieldDetectionFull`) is on `lensFieldDetection`, `lensRemoteModels`, `lensLite`, and `lensDemo`. This product's Gradle flavor uses `fieldDetectionStub` unless the size note below says otherwise. The validate-client boolean is `detect_local_fields_on` (`local_fields_values_result_on` controls returning values). Demo package `com.veryfi.lensdemo` and `com.iqboxyinc.iqboxy` are forced on in `PartnerHelper`. That package exception is not a partner entitlement.

`documentFieldsToDetect` targets receipts, long receipts, invoices, and other. It does not read card number, name, date, or CVC. Those are `creditCardDetectCard*`. This flavor is `fieldDetectionStub`.


## Local card privacy

`UploadDocumentsService.checkExtractionEngine` sends Cloud image upload only for `VeryfiCloudAPI`. Credit cards are forced to `VeryfiInApp`, which calls `notifyServerOfDocumentReady` and then `CardExtraction.getCardData`. The card JSON is delivered to the host with `BroadcastHelper.sendBroadcastDocumentReadyJsonString`. The doc-ready body includes document metadata (type, corners, image size, LCD scores, device id) and does not include the PAN field. Keep `VeryfiInApp`. Do not flip the engine to upload card photos for extraction.


## Unconfirmed or easy to over-claim

| Item | What the code shows | Status |
|------|---------------------|--------|
| `cleanLensStorage()` and `veryfi/zip/` | Clears Lens pictures and base-dir zips | **Unconfirmed** that retained zip packages are removed |
| Blurred files inside `uploadImages` | `nonInteractiveBlurCheckIsOn = false` skips adding blurred images | **Unconfirmed** whether a callback fires, and whether `blurDetectionIsOn` also gates it |
| `closeCameraOnSubmit` and `submitWithoutClose` both true | Two exit paths | Winner **Unconfirmed** |
| `showHelpButton` together with `showInfoButton` | Separate views; info also shows the browse icon | Visual fit **Unconfirmed** |
| Overriding AAR string resources by name | Standard resource merge | Resource names are not a documented contract (**Unconfirmed**) |
| `tools:node="remove"` on merged permissions | Standard manifest merger | **Unconfirmed** that every Lens path handles the missing permission |
| `useLegacyModels` | Default **true** | No partner contract. Leave the default. The Demo checks preset sets it false; do not copy that |
| `packageMaxScans` | Property initializer is **5**. KDoc says 4 | Trust the initializer |
| Cached region on configure | `validateClientId` can return true with no network | **Unconfirmed** whether `lens_settings` are reapplied from cache on that path |
| `attachDocument` | Starts an attach flow | Veryfi-owned. **Unconfirmed** for partners |

## Do not generate

- iOS-only API: `VeryfiLens.shared()`, `utils`, view-controller embedding, `showCrop`, `headlessUpload`, SwiftUI helpers, `shareExtensionModeIsOn`, `customLensStrings`, `backupDocsToiCloud`, `showiCloudBackupOption`, `VeryfiInfoView`.
- A host `Fragment` that embeds the Lens camera. Lens UI starts its own activities (`CaptureActivity`, `GalleryActivity`, `DocumentBrowserActivity`, `SubmitActivity`, `InfoActivity`).
- `DocumentType` values that are not in the Android enum. There is no `dictation` or `bank_letter_and_check`.
- `com.veryfi.lens.core.VeryfiLensCore` (`showEmail` / `showDictation`). Dictation is obsolete and Core is not a partner artifact.
- Demo internals as the host API: `SettingsHolder`, Demo camera analyzers, `ImageProxyUtils`, and `VeryfiLensHelper` (the SDK's internal settings holder).
- `invalid_credentials` as a delegate status. Android reports configuration failure as `configure` callback `false`.
- Android failure status `"fail"`. The string is `"failed"`.

## Strings

Android has no `customLensStrings`. Lens copy ships as string resources in the AAR. See [customizable strings](https://docs.veryfi.com/lens/mobile/settings/customizable_strings/). Colors are hex strings (`#RRGGBB` or `#AARRGGBB`). Fonts are `String?` names. `brandImage` and `notificationSmallIcon` are drawable resource ids.

