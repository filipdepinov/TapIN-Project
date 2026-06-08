package com.tapin.teacher.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tapin.teacher.data.remote.dto.CourseDto
import com.tapin.teacher.databinding.ItemCourseBinding

class CourseAdapter(
    private val onClick: (CourseDto) -> Unit
) : ListAdapter<CourseDto, CourseAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val b: ItemCourseBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(course: CourseDto) {
            b.tvCourseCode.text    = course.code
            b.tvCourseName.text    = course.name
            b.tvDescription.text   = course.description ?: ""
            b.tvEnrolled.text      = "${course.count?.enrollments ?: 0} students enrolled"
            b.root.setOnClickListener { onClick(course) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CourseDto>() {
            override fun areItemsTheSame(a: CourseDto, b: CourseDto) = a.id == b.id
            override fun areContentsTheSame(a: CourseDto, b: CourseDto) = a == b
        }
    }
}
