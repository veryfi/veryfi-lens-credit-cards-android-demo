package com.veryfi.lens.credit.cards.demo.logs

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lriccardo.timelineview.TimelineAdapter
import com.lriccardo.timelineview.TimelineView
import com.veryfi.lens.credit.cards.demo.databinding.LogsListItemBinding

class LogsAdapter : RecyclerView.Adapter<LogsViewHolder>(),
    TimelineAdapter {

    private var logsItems: ArrayList<Log> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder {
        val itemBinding = LogsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogsViewHolder(itemBinding)
    }

    fun addItem(item: Log) {
        logsItems.add(item)
    }

    fun setLogsList(logs: ArrayList<Log>) {
        logsItems = logs
    }

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        holder.onBind(logsItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int = logsItems.size

    override fun getIndicatorStyle(position: Int): TimelineView.IndicatorStyle? {
        return TimelineView.IndicatorStyle.Checked
    }

    override fun getLineStyle(position: Int): TimelineView.LineStyle? {
        return super.getLineStyle(position)
    }

    override fun getLinePadding(position: Int): Float? {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f,
            Resources.getSystem().displayMetrics
        )
    }
}