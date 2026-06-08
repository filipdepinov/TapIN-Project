package com.tapin.student.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.tapin.student.R
import com.tapin.student.data.remote.dto.CourseAttendanceDto
import com.tapin.student.data.remote.dto.SessionDto
import com.tapin.student.databinding.FragmentAttendanceBinding
import com.tapin.student.databinding.ItemCourseAttendanceBinding
import com.tapin.student.databinding.ItemSessionRowBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AttendanceViewModel by viewModels()

    // Input parser for ISO-8601 dates
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val displayFormat = SimpleDateFormat("EEE, d MMM yyyy  HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRetry.setOnClickListener { viewModel.load() }

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible  = state is AttendanceUiState.Loading
                    binding.layoutError.isVisible  = state is AttendanceUiState.Error
                    binding.scrollContent.isVisible = state is AttendanceUiState.Success

                    when (state) {
                        is AttendanceUiState.Success -> renderCourses(state.courses)
                        is AttendanceUiState.Error   -> { /* error view already visible */ }
                        else -> { }
                    }
                }
            }
        }
    }

    private fun renderCourses(courses: List<CourseAttendanceDto>) {
        val container = binding.containerCourses
        container.removeAllViews()

        if (courses.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = getString(R.string.no_attendance_data)
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
            container.addView(tv)
            return
        }

        courses.forEach { course ->
            val cardBinding = ItemCourseAttendanceBinding.inflate(
                layoutInflater, container, false
            )

            cardBinding.tvCourseCode.text = course.courseCode
            cardBinding.tvCourseName.text = course.courseName
            cardBinding.tvSessions.text   = getString(
                R.string.sessions_attended, course.attended, course.totalSessions
            )

            val rate = course.attendanceRate.toInt()
            cardBinding.progressRate.progress = rate
            cardBinding.tvRatePct.text = "$rate%"

            // Color-code the rate bar
            val color = when {
                rate >= 80 -> ContextCompat.getColor(requireContext(), R.color.success)
                rate >= 60 -> ContextCompat.getColor(requireContext(), R.color.warning)
                else       -> ContextCompat.getColor(requireContext(), R.color.error)
            }
            cardBinding.progressRate.setIndicatorColor(color)
            cardBinding.tvRatePct.setTextColor(color)

            // Render session rows
            course.sessions.forEach { session ->
                renderSessionRow(cardBinding, session)
            }

            container.addView(cardBinding.root)
        }
    }

    private fun renderSessionRow(
        cardBinding: ItemCourseAttendanceBinding,
        session: SessionDto
    ) {
        val rowBinding = ItemSessionRowBinding.inflate(
            layoutInflater, cardBinding.containerSessions, false
        )

        // Format date
        val dateStr = try {
            val date = isoFormat.parse(session.startedAt)
            if (date != null) displayFormat.format(date) else session.startedAt
        } catch (e: Exception) { session.startedAt }

        rowBinding.tvSessionDate.text = dateStr

        if (session.present) {
            rowBinding.viewDot.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.success)
            )
            rowBinding.tvStatus.text = "PRESENT"
            rowBinding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
            rowBinding.tvStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.success_light))
        } else {
            rowBinding.viewDot.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.error)
            )
            rowBinding.tvStatus.text = "ABSENT"
            rowBinding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
            rowBinding.tvStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.error_light))
        }

        cardBinding.containerSessions.addView(rowBinding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
