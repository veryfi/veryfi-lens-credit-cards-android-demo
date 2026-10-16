# Veryfi Lens Android — Credit Cards — settings

Source of truth: Veryfi Lens Android SDK **2.7.0.6**, commit `7f33726c`. Names below were read from that tree. Items not fully confirmed are marked **Unconfirmed**. Do not copy repository passwords, keystores, or Demo-only types into the host app.


Always construct a fresh `VeryfiLensSettings()`. Android has no `VeryfiLens*Settings` subclasses. Pass that instance to `configure`. The SDK mutates it.

```kotlin
import com.veryfi.lens.helpers.DocumentType
import com.veryfi.lens.helpers.VeryfiLensSettings

fun creditCardSettings() = VeryfiLensSettings().apply {
    ignoreRemoteSettings = true            // default false; dashboard lens_settings overwrite this object during configure
    backupDocsToGallery = false            // default true; copies every scan into the device gallery
    moreMenuIsOn = false                   // default true; stored user prefs then replace host capture flags
    moreSettingsMenuIsOn = false           // default true
    autoCaptureOtherIsOn = false           // default true; OTHER auto-captures even when autoCaptureIsOn is false
    showNoDocumentDetectedWarning = false  // default true
    dictateIsOn = false                    // default true; obsolete voice capture, not a partner feature
    locationServicesIsOn = false           // default false; true raises a runtime location request
    glareDetectionIsOn = false             // default true; the glare gate is a weak algorithmic check

    dataExtractionEngine = VeryfiLensSettings.ExtractionEngine.VeryfiInApp // also forced when type[0] is CREDIT_CARD
    documentTypes = arrayListOf(DocumentType.CREDIT_CARD)
    showDocumentTypes = false
    autoCaptureIsOn = true                 // forced true when the card type is selected on camera
    galleryIsOn = false                    // forced false when stored credit-card prefs load
    stitchIsOn = false                     // default true
    autoLightDetectionIsOn = false         // default true; also forced false by checkCreditCardSettings()
    autoRotateIsOn = true                  // default false
    cropLayoutIsOn = true                  // default false
    cropLayoutWithAutoDetection = true     // default false; Android hybrid needs both flags
    autoDocDetectionAndCropIsOn = true     // default true
    cropLayoutWidth = 80                   // default 80
    cropLayoutHeight = 25                  // default 35
    cropLayoutCornerRadius = 15            // default 0
    cropLayoutStroke = 16                  // default 0
    cropLayoutBorderColor = "#FFFFFFFF"    // default #00000000
    creditCardMarginTop = 100              // default 100
    creditCardMarginBottom = 0             // default 0
    creditCardDetectCardNumber = true      // default true
    creditCardDetectCardHolderName = true  // default true
    creditCardDetectCardDate = true        // default true
    creditCardDetectCardCVC = true         // default true
    creditCardAutoCaptureMode = VeryfiLensSettings.CreditCardAutoCaptureMode.Normal // Speed, Normal, Accuracy
}

```

## Defaults versus this recipe

Android initializers below are the property defaults in `VeryfiLensSettings`.

| Property | Android default | This recipe | Why |
|----------|-----------------|-------------|-----|
| `ignoreRemoteSettings` | false | true | Dashboard `lens_settings` would overwrite this object during configure |
| `backupDocsToGallery` | true | false | Default copies every scan into the device gallery |
| `moreMenuIsOn` | true | false | Stored user prefs would replace host capture flags on later configure calls |
| `moreSettingsMenuIsOn` | true | false | Settings sheet inside More |
| `autoCaptureOtherIsOn` | true | false | `OTHER` auto-captures even when `autoCaptureIsOn` is false |
| `showNoDocumentDetectedWarning` | true | false | Skip the no-document alert |
| `dictateIsOn` | true | false | Obsolete voice control; default is on |
| `glareDetectionIsOn` | true | false | Android default glare gate is a weak check |
| `locationServicesIsOn` | false | false | Leave off unless the host needs lat/lng on the package |

| `dataExtractionEngine` | `VeryfiCloudAPI` | `VeryfiInApp` | Forced again when `documentTypes[0]` is `CREDIT_CARD` |
| `autoCaptureIsOn` | false | true | Camera selection of `CREDIT_CARD` sets it true |
| `galleryIsOn` | true | false | Forced false on the stored-prefs path for credit cards |
| `stitchIsOn` | true | false | Single card frame |
| `autoLightDetectionIsOn` | true | false | Forced false for credit cards |
| `cropLayoutIsOn` | false | true | Fixed frame. Hybrid also needs `cropLayoutWithAutoDetection` |
| `cropLayoutWithAutoDetection` | false | true | Green box inside the frame |
| `cropLayoutHeight` | 35 | 25 | Card-shaped frame (percent) |
| `creditCardAutoCaptureMode` | `Normal` | `Normal` | `Speed` or `Accuracy` when the host asks |


Hybrid crop on Android is `cropLayoutIsOn = true` and `cropLayoutWithAutoDetection = true` with `autoDocDetectionAndCropIsOn = true`. A fixed frame with no ML crop is `cropLayoutIsOn = true` and `autoDocDetectionAndCropIsOn = false`. `cropLayoutWithAutoDetection` without `cropLayoutIsOn` draws a frame but crops with full-frame detection.

`CardExtraction` JSON keys: `card_number`, `card_name`, `card_type`, `card_dates`, `card_cvc`, `card_uuid`, `is_invalid`. The UI success path parses `CardExtraction.getCardData` into `data` when that string is JSON. Log one payload before binding the host UI (**Unconfirmed** which keys are populated on every capture).

Gallery and `uploadImages` are the wrong tool for reading a card. Leave `galleryIsOn` false. Do not document `uploadImages` as the card reader.


## Suggestions

- Keep `moreMenuIsOn` false so a returning user cannot turn gallery backup on.
- Choose `CreditCardAutoCaptureMode` explicitly even though Normal is the default.
- For a host-owned camera, switch artifacts to `com.veryfi.lens.headless:credit-cards-sdk:2.7.0.6` and do not call `VeryfiLens.showCamera()`.
- There is no Remote Models size win: `checkLensFull` rejects `CREDIT_CARD`. Measure the installed app with this artifact linked and removed.

