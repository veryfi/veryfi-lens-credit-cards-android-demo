package com.veryfi.lens.credit.cards.demo.logs

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lriccardo.timelineview.TimelineDecorator
import com.veryfi.lens.VeryfiLens
import com.veryfi.lens.VeryfiLensDelegate
import com.veryfi.lens.credit.cards.demo.R
import com.veryfi.lens.credit.cards.demo.databinding.ActivityLogsBinding
import com.veryfi.lens.credit.cards.demo.helpers.ThemeHelper
import org.json.JSONException
import org.json.JSONObject

class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding
    private lateinit var adapter: LogsAdapter
    private var logsEventData: ArrayList<Log> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applicationContext?.let { ThemeHelper.setSecondaryColorToStatusBar(this, it) }
        setUpVeryfiLensDelegate()
        VeryfiLens.showCamera()
        setUpToolBar()
        loadData()
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_vector_close_shape)
        binding.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.timelineRv.let {
            it.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapter = LogsAdapter()
            it.adapter = adapter

            val colorPrimary = ThemeHelper.getPrimaryColor(this)
            it.addItemDecoration(
                TimelineDecorator(
                    position = TimelineDecorator.Position.Left,
                    indicatorColor = colorPrimary,
                    lineColor = colorPrimary
                )
            )

            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    (it.layoutManager as? LinearLayoutManager)?.let {

                    }
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showLogs(json: JSONObject) {
        val log = Log()
        log.message = json
        val status = if (json.has(STATUS)) json.getString(STATUS) else ""

        when (status) {
            START -> log.title = resources.getString(R.string.logs_start_uploading)
            IN_PROGRESS -> {
                when (json.getString(MSG)) {
                    IMG_THUMBNAIL -> log.title = resources.getString(R.string.logs_thumbnail)
                    IMG_ORIGINAL -> log.title =
                        resources.getString(R.string.logs_original_image)
                    PROGRESS -> log.title = resources.getString(R.string.logs_extraction)
                }
            }
            DONE -> log.title = resources.getString(R.string.logs_result)
            REMOVED -> {
                log.title = resources.getString(R.string.logs_remove_cache_data)
            }
            EXCEPTION -> log.title = resources.getString(R.string.logs_exception)
            ERROR -> log.title = resources.getString(R.string.logs_error)
            FAILED -> log.title = resources.getString(R.string.logs_failed)
            else -> log.title = "Other"
        }

        if (!status.equals(CLOSE)) {
            logsEventData.add(log)
            adapter.addItem(log)
            adapter.notifyDataSetChanged()
            binding.timelineRv.scrollToPosition(adapter.itemCount - 1)
        } else {
            if (logsEventData.size == 0) {
                finish()
            }
        }
    }

    private fun setUpVeryfiLensDelegate() {
        VeryfiLens.setDelegate(object : VeryfiLensDelegate {
            override fun veryfiLensClose(json: JSONObject) {
                showLogs(json)
            }

            override fun veryfiLensError(json: JSONObject) {
                showLogs(json)
            }

            override fun veryfiLensSuccess(json: JSONObject) {
                showLogs(json)
            }

            override fun veryfiLensUpdate(json: JSONObject) {
                showLogs(json)
            }
        })
    }

    private inner class VeryfiServiceJsonReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                val json = JSONObject()
                for (key in bundle.keySet()) {
                    val value = bundle.get(key)
                    if (value != null) {
                        try {
                            json.put(key, JSONObject.wrap(bundle.get(key)))
                        } catch (e: JSONException) {
                            //Handle exception here
                        }
                    }
                }
                showLogs(json)
            }
        }
    }

    companion object {
        const val STATUS = "status"
        const val START = "start"
        const val IN_PROGRESS = "inprogress"
        const val MSG = "msg"
        const val IMG_THUMBNAIL = "img_thumbnail_path"
        const val IMG_ORIGINAL = "img_original_path"
        const val PROGRESS = "progress"
        const val DONE = "done"
        const val REMOVED = "removed"
        const val EXCEPTION = "exception"
        const val ERROR = "error"
        const val FAILED = "failed"
        const val CLOSE = "close"
    }
}