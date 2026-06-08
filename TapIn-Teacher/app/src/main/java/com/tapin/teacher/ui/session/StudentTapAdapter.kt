package com.tapin.teacher.ui.session

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tapin.teacher.data.local.entity.AttendanceRecord
import com.tapin.teacher.databinding.ItemStudentTapBinding
import java.text.SimpleDateFormat
import java.util.*

class StudentTapAdapter : ListAdapter<AttendanceRecord, StudentTapAdapter.ViewHolder>(DIFF) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentTapBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class ViewHolder(private val b: ItemStudentTapBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(record: AttendanceRecord, position: Int) {
            b.tvPosition.text    = "#$position"
            b.tvStudentName.text = record.studentName
            b.tvStudentId.text   = record.studentNumber ?: ""
            b.tvTapTime.text     = timeFormat.format(Date(record.tappedAt))
            b.tvSynced.text      = if (record.synced) "✓" else "⏳"
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AttendanceRecord>() {
            override fun areItemsTheSame(a: AttendanceRecord, b: AttendanceRecord) =
                a.localId == b.localId
            override fun areContentsTheSame(a: AttendanceRecord, b: AttendanceRecord) =
                a == b
        }
    }
}
