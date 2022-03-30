package com.veryfi.lens.credit.cards.demo

import android.app.Application
import com.veryfi.lens.VeryfiLens
import com.veryfi.lens.VeryfiLensCredentials
import com.veryfi.lens.VeryfiLensSettings

class Application: Application() {

    companion object {
        //REPLACE YOUR KEYS HERE
        const val CLIENT_ID = "xxx"
        const val AUTH_USERNAME = "xxx"
        const val AUTH_API_KEY = "xxx"
        const val URL = "https://xxx.xxx"
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
        veryfiLensSettings.documentTypes = arrayListOf("receipt")
        veryfiLensSettings.galleryIsOn = false
        veryfiLensSettings.moreMenuIsOn = false
        veryfiLensSettings.showDocumentTypes = true

        //configure lens
        VeryfiLens.configure(this, veryfiLensCredentials, veryfiLensSettings)
    }
}