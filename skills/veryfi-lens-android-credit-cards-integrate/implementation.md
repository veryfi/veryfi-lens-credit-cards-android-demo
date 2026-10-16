# Veryfi Lens Android — Credit Cards — implementation

Source of truth: Veryfi Lens Android SDK **2.7.0.6**, commit `7f33726c`. Names below were read from that tree. Items not fully confirmed are marked **Unconfirmed**. Do not copy repository passwords, keystores, or Demo-only types into the host app.


Lens UI façade: `com.veryfi.lens.VeryfiLens`. Delegate: `com.veryfi.lens.VeryfiLensDelegate`. Shared types in `com.veryfi.lens.helpers`: `VeryfiLensCredentials`, `VeryfiLensSettings`, `DocumentType`.

## Credentials

```kotlin
import android.app.Application
import com.veryfi.lens.VeryfiLens
import com.veryfi.lens.VeryfiLensDelegate
import com.veryfi.lens.helpers.DocumentType
import com.veryfi.lens.helpers.VeryfiLensCredentials
import com.veryfi.lens.helpers.VeryfiLensSettings
import org.json.JSONObject

val credentials = VeryfiLensCredentials().apply {
    clientId = BuildConfig.VERYFI_CLIENT_ID
    username = BuildConfig.VERYFI_USERNAME
    apiKey = BuildConfig.VERYFI_API_KEY
    // url = "..." // only when Veryfi assigned a URL
}
```

`clientId`, `username`, `apiKey`, and `url` are `var String` defaulting to `""`. A non-empty invalid `url` shows `The URL [..] is not valid.` and configure continues. `hasEmptyCredentialFields()` is true only when every field is blank. Credential-less scanning is armed for the Demo package only. Partners need credentials. Android has no `invalid_credentials` delegate error. A bad configuration returns `ok == false` from `configure`, and later upload errors.


## Configure, then present

```kotlin
VeryfiLens.setDelegate(LensDelegate)
VeryfiLens.configure(application, credentials, creditCardSettings()) { ok ->
    if (ok) {
        VeryfiLens.showCamera()
    } else {
        showConfigError()
    }
}
```

`configure(application, credentials, settings)` and `configure(application, credentials, settings, callback)` are both `@JvmStatic`. Use the callback overload. `VeryfiLens.kt` then:

1. Loads native libraries, stores credentials, and keeps **the same** settings instance (`settings ?: VeryfiLensSettings()`).
2. `checkCreditCardSettings()`: when `documentTypes[0]` is `CREDIT_CARD`, forces `dataExtractionEngine = VeryfiInApp` and `autoLightDetectionIsOn = false`.
3. `checkUserUISettings()`: when stored preferences exist **and** `moreMenuIsOn` is true, stored values replace host `autoDocDetectionAndCropIsOn`, `blurDetectionIsOn`, `glareDetectionIsOn`, `autoLightDetectionIsOn`, `autoCaptureIsOn`, `backupDocsToGallery`, `closeCameraOnSubmit`, and `batchUploadOnCamera`. Otherwise the host values are written to preferences. On that stored-prefs path, `galleryIsOn` is forced false for a first type of `CREDIT_CARD` or `CODE`, and `autoCaptureIsOn` is forced false for a first type of `LONG_RECEIPT`.
4. Shows `Device Nexus 5X not supported` on a Nexus 5X.
5. `PartnerHelper.validateClientId`: returns true immediately when a region is cached (no network). With no network and no cache, returns false. Unless `ignoreRemoteSettings = true`, a `lens_settings` payload mutates the settings object. Signed v3 verification runs only when the active host is `api.veryfi.com` or `devapi.veryfi.com`. Any other `overrideBaseUrl` uses unsigned v2. The host does not implement signature checks.

Rebuild a fresh `VeryfiLensSettings()` and call `configure` again to change configuration. Calling a launcher before configure logs that the application is not initialized and does not invoke the delegate.




## Delegate

```kotlin
object LensDelegate : VeryfiLensDelegate {
    override fun veryfiLensClose(json: JSONObject) {
        // status "close". Queued uploads may continue.
    }

    override fun veryfiLensUpdate(json: JSONObject) {
        when (json.optString("status")) {
            "start" -> { /* package_id, thumbnail paths */ }
            "inprogress" -> { /* progress */ }
            "failed" -> { /* json.optString("error") */ }
        }
    }

    override fun veryfiLensError(json: JSONObject) {
        val message = json.optString("msg", json.optString("error"))
    }

    override fun veryfiLensSuccess(json: JSONObject) {
        val card = json.optJSONObject("data") // CardExtraction JSON parsed by the SDK
    }
}
```

`setDelegate` stores a **strong** reference on the `VeryfiLens` object and registers an EventBus subscriber. `removeDelegate()` clears both. Use an application-scoped delegate, or call `removeDelegate()` from `onDestroy`, so an Activity is not retained. Callbacks are delivered on the main thread. When `data` is a JSON string, the SDK parses it into a nested `JSONObject` before delivery. `detected_objects` and `lcd_scores` on update are parsed into `JSONArray`s.


## Entry points

Present UI only after `configure` returns `true`. `showCamera()` is debounced and is the only launcher that runs `FlavorCheckerHelper`. A disallowed `documentTypes` entry shows the Toast `Please use only valid document types, refer to hub docs: Lens for Credit Cards`, logs the error, and does not open the camera (no delegate callback). `documentTypes == null` shows `Please specify the document types`.

| Need | Call |
|------|------|
| Lens camera | `VeryfiLens.showCamera()` |
| Lens gallery UI | `VeryfiLens.showGallery()` |
| System file picker UI | `VeryfiLens.showDocumentBrowser()` |
| Files the app already holds, no Lens screen | `VeryfiLens.uploadImages(uris, isSameTransaction)` or `uploadImages(paths, isSameTransaction)` |
| Close the capture activity | `VeryfiLens.closeCamera()` |

`showGallery()` and `showDocumentBrowser()` do not call `FlavorCheckerHelper`. They still use the configured `documentTypes`. Do not start two Lens flows at once.

In-camera import controls are independent:

- More-menu Import card: `moreMenuIsOn = true` and `browseIsOn = true` (`browseIsOn` defaults to **true**, and only affects that card).
- Top-right browse icon: `showBrowserButton = true`. A non-null `showInfoButton` also shows that icon and changes the close icon.
- Gallery button Photos/Files menu: `galleryIsOn = true` and `galleryButtonWithFilesIsOn = true`.

Do not enable the gallery button, the browse icon, or `uploadImages` for card PANs. `showGallery()` and `showDocumentBrowser()` exist on `VeryfiLens` but this product reads cards from the Lens camera. `uploadImages` runs the UI upload service; it is not `VeryfiLensHeadless.processCreditCard`.

### No-UI `uploadImages` (Lens UI SDK)

```kotlin
VeryfiLens.uploadImages(uris: Array<Uri>?, isSameTransaction: Boolean = true)
VeryfiLens.uploadImages(paths: Array<String>?, isSameTransaction: Boolean = true)
```

Same artifact, same `configure`, same `VeryfiLensDelegate`, same `UploadDocumentsService` as the camera. `isSameTransaction = true` builds one package; `false` builds one package per image.

| | `Uri` overload | `paths` overload |
|--|--|--|
| Permission | API < 33: storage must already be granted, else `storage_permission_missing` | no check |
| Images | decoded; optional `autoCropGalleryIsOn` | `BitmapFactory.decodeFile`; optional auto-crop |
| PDF and other files | copied and packaged, typed as `documentTypes[0]` (or `RECEIPT`) | ignored, because they are not bitmaps |
| `null` paths | — | falls back to Pictures `/image.jpg` |
| Nothing usable | — | no callback |

`allowSubmittingScreenshots = false` rejects screenshots with `image_is_screenshot`. `nonInteractiveBlurCheckIsOn = true` reports blur as `blurred_image`. With the check left false, blurred images may be dropped with no callback (**Unconfirmed**).

`uploadImages` is the Lens UI file pipeline. It is a different product from the Headless Receipts SDK (`com.veryfi.lens.headless:receipts-sdk`) and the Headless Credit Cards SDK (`com.veryfi.lens.headless:credit-cards-sdk`). Those artifacts use `VeryfiLensHeadless` on a camera the host owns. They do not implement `uploadImages`, and they do not call Process a Document.

### Queue helpers

| Call | Effect |
|------|--------|
| `packagesInQueue(): ArrayList<String>` | package ids still queued |
| `retryUpload(packageId)` / `retryAll()` | restart upload through the foreground service |
| `discardUpload(packageId)` / `discardAll()` | drop queued packages |
| `cleanLensStorage()` | `discardAll()` plus clear local Lens pictures and base-dir zips |
| `allowReuploadingFiles(fingerprint)` / `(file)` / `(uri)` | clear a hash stored for `sameFileUploadPreventionPolicy` |
| `updateScanSessionCounter(count)` | set the camera badge when `scanSessionCounterIsOn` is true and `autoScanSessionCounterIsOn` is false; negative values clamp to 0; other modes no-op |

The badge stays hidden unless `scanSessionCounterIsOn` is true. With `autoScanSessionCounterIsOn` true (the default), Lens updates it from successful submissions. Submit moves the package into a processing session and starts a new capture session before upload, so opening the camera again does not show or drop a package already handed to upload.

`cleanLensStorage()` clearing `veryfi/zip/` retained packages is **Unconfirmed**. Delete `local_package_path` with `File(path).delete()` after the host is done with it.

Public methods that are not partner contracts: `attachDocument` (Veryfi-owned attach mode, **Unconfirmed**), `delegateLensSuccess`, `delegateLensUpdate`, `removeHandsFree`, `checkCloseCamera`, `disableViaVoiceAnimation`, `disableBlurDetection`. Do not call Demo types (`SettingsHolder`, Demo analyzers) from the host.

