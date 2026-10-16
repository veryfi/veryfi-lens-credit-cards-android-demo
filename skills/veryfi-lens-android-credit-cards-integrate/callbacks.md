# Veryfi Lens Android — Credit Cards — callbacks

Source of truth: Veryfi Lens Android SDK **2.7.0.6**, commit `7f33726c`. Names below were read from that tree. Items not fully confirmed are marked **Unconfirmed**. Do not copy repository passwords, keystores, or Demo-only types into the host app.


Delegate methods, all required except `onHelpButtonClicked` (default empty): `veryfiLensClose`, `veryfiLensUpdate`, `veryfiLensError`, `veryfiLensSuccess`. Argument type is `org.json.JSONObject`. Delivery is the main thread.

Common keys: `package_id`, `upload_id`, `receipt_id`, `document_type`, `data`, `source`, `status`, `connectivity`, `progress`, `exception`, `error`, `msg`, `img_original_data`, `img_original_path`, `img_original_paths`, `img_thumbnail_path`, `img_thumbnail_paths`, `img_thumbnail_data`, `img_stitched_pdf_path`, `img_check_front_tiff_path`, `img_check_back_tiff_path`, `file_hashes`, `img_paths`, `images`, `framework-version`, `framework-build`, `session_scan_count`, `scan_session_counter`, `queue_count`, `scan_to_pdf_pages`, `device_id`, `type`, `empty_fields`, `local_package_path`.

| Status | Callback | Meaning |
|--------|----------|---------|
| `start` | update | Package created |
| `inprogress` | update | Uploading. Progress also arrives as `msg = "progress"` |
| `failed` | update or error | Android spelling |
| `error` | error | `error` / `msg` |
| `clear_package` | update | Local package cleaned |
| `close` | close | Lens UI closed |
| `done` | success | Finished |

File strings include `invalid_file`, `empty_file`, `image_too_small`, `invalid_pdf_file`, `image_is_screenshot`, `blurred_image`, `storage_permission_missing`.

| Engine | Success body |
|--------|----------------|
| `VeryfiCloudAPI` | Veryfi API result in `data` |
| `VeryfiInApp` + `CREDIT_CARD` | On-device card JSON parsed into `data` (`CardExtraction.getCardData`) |
| `VeryfiInApp` + `BARCODE` | On-device barcodes JSON parsed into `data` |
| `VeryfiInApp` + `CODE` | Top-level `ocr_uuid`, `ocr_text`, `ocr_score`, `ocr_image`, `ocr_type` |
| `VeryfiInApp` + identity verification | `similarity_score`, `face1_path`, `face2_path`, `is_match` |
| `VeryfiInApp` + any other type | Plain success, no extraction |
| `None`, upload without processing off | `data.local_package_path` when files are retained |
| `None`, upload without processing on | `bucket` and `package_path` |

In-app and `None` still post doc-ready metadata (document information, no image bytes) via `notifyServerOfDocumentReady`.


UI credit-card success: `data` is the parsed `CardExtraction.getCardData` object when the SDK can parse it. Keys defined on `CardExtraction`: `card_number`, `card_name`, `card_type`, `card_dates` (array), `card_cvc`, `card_uuid`, `is_invalid`. Which of those are filled on a given scan should be logged once (**Unconfirmed** completeness).

Privacy: `ExtractionEngine.VeryfiInApp` skips `notifyServerBeforeUpload`. `CardExtraction.getCardData` is broadcast locally via `sendSuccessNotificationJsonString`. `notifyServerOfDocumentReady` still POSTs session metadata (`sdk` = `android`, extraction engine, document information). The PAN is not one of those metadata fields.

