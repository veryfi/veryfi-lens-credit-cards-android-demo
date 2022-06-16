package com.veryfi.lens.credit.cards.demo

import android.app.Application
import com.veryfi.lens.VeryfiLens
import com.veryfi.lens.VeryfiLensCredentials
import com.veryfi.lens.VeryfiLensSettings
import com.veryfi.lens.helpers.DocumentType

class Application: Application() {

    companion object {
        //REPLACE YOUR KEYS HERE
        const val CLIENT_ID = BuildConfig.VERYFI_CLIENT_ID
        const val AUTH_USERNAME = BuildConfig.VERYFI_USERNAME
        const val AUTH_API_KEY = BuildConfig.VERYFI_API_KEY
        const val URL = BuildConfig.VERYFI_URL
    }

    override fun onCreate() {
        super.onCreate()
        //set credentials
        val veryfiLensCredentials = VeryfiLensCredentials()
        veryfiLensCredentials.clientId = CLIENT_ID
        veryfiLensCredentials.username = AUTH_USERNAME
        veryfiLensCredentials.apiKey = AUTH_API_KEY
        veryfiLensCredentials.url = URL

        //optional settings
        val veryfiLensSettings = VeryfiLensSettings()
        veryfiLensSettings.autoCaptureIsOn = true
        veryfiLensSettings.autoRotateIsOn = true
        veryfiLensSettings.autoSubmitDocumentOnCapture = true
        veryfiLensSettings.documentTypes = arrayListOf(DocumentType.CREDIT_CARD)
        veryfiLensSettings.galleryIsOn = false
        veryfiLensSettings.moreMenuIsOn = false
        veryfiLensSettings.showDocumentTypes = true

        //configure lens
        VeryfiLens.configure(this, veryfiLensCredentials, veryfiLensSettings)
    }
}