package com.tapin.teacher.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tapin.teacher.databinding.ItemSessionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<SessionGroup, HistoryAdapter.ViewHolder>(DIFF) {

    private val dateFormat = SimpleDateFormat("EEE d MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemSessionHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemSessionHistoryBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(group: SessionGroup) {
            b.tvCourseCode.text  = group.courseCode
            b.tvCourseName.text  = group.courseName
            b.tvDate.text        = dateFormat.format(Date(group.earliestTap))
            b.tvCount.text       = "${group.records.size} students"
            val synced   = group.records.count { it.synced }
            val unsynced = group.records.size - synced
            b.tvSyncStatus.text  = if (unsynced == 0) "All synced ✓" else "$unsynced pending"
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SessionGroup>() {
            override fun areItemsTheSame(a: SessionGroup, b: SessionGroup) =
                a.sessionId == b.sessionId
            override fun areContentsTheSame(a: SessionGroup, b: SessionGroup) =
                a == b
        }
    }
}
