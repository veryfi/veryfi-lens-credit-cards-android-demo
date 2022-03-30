package com.veryfi.lens.credit.cards.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.veryfi.lens.VeryfiLens
import com.veryfi.lens.VeryfiLensDelegate
import com.veryfi.lens.credit.cards.demo.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpVeryfiLens()
        binding.openCameraButton.setOnClickListener {
            VeryfiLens.showCamera()
        }
    }

    private fun setUpVeryfiLens() {
        VeryfiLens.setDelegate(object : VeryfiLensDelegate {
            override fun veryfiLensClose(json: JSONObject) {
                showLogs(json.toString(2))
            }

            override fun veryfiLensError(json: JSONObject) {
                showLogs(json.toString(2))
            }

            override fun veryfiLensSuccess(json: JSONObject) {
                showLogs(json.toString(2))
            }

            override fun veryfiLensUpdate(json: JSONObject) {
                showLogs(json.toString(2))
            }
        })
    }

    private fun showLogs(log: String) {
        val text = binding.textViewLogs.text.toString() + log
        binding.textViewLogs.text = text
    }

}
