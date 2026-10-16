# Validation checklist

Run this before delivering host code. Fix conflicts by removing extra flags.

- [ ] Dependency is `com.veryfi.lens:veryfi-lens-credit-cards-sdk:2.7.0.6`
- [ ] `documentTypes` is only CREDIT_CARD and the engine is VeryfiInApp
- [ ] `galleryIsOn` is false and `autoCaptureIsOn` is true
- [ ] Hybrid crop sets both `cropLayoutIsOn` and `cropLayoutWithAutoDetection`
- [ ] Privacy note states on-device JSON plus metadata doc-ready, no Cloud image upload
- [ ] `uploadImages` is not used as the card reader and is not described as Headless
- [ ] Fraud is backend-gated if mentioned
- [ ] Installed app size, not AAR size
- [ ] No `VeryfiLensHelper` mutation

- [ ] Every relative link in the generated host notes stays inside this skill folder
- [ ] Unconfirmed behavior the host depends on is labeled **Unconfirmed** in the delivery note
- [ ] Host `<application>` sets `android:allowBackup="false"` and `tools:replace="android:allowBackup"` so a new-project `true` does not fail the manifest merger
- [ ] No repository password, Demo `SettingsHolder` preset, or `VeryfiLensHelper` mutation is in the host code
