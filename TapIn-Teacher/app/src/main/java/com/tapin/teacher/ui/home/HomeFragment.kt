package com.tapin.teacher.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tapin.teacher.R
import com.tapin.teacher.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var coursesAdapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupClickListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.rvCourses.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        coursesAdapter = CourseAdapter { course ->
            val action = HomeFragmentDirections.actionHomeToSession(
                courseId   = course.id,
                courseName = course.name,
                courseCode = course.code
            )
            findNavController().navigate(action)
        }
        binding.rvCourses.adapter = coursesAdapter
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    findNavController().navigate(R.id.action_home_to_history)
                    true
                }
                R.id.action_logout -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.logout()
                        findNavController().navigate(R.id.action_home_to_login)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSync.setOnClickListener { viewModel.syncNow() }
        binding.btnRetry.setOnClickListener { viewModel.loadCourses() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Greeting
                    binding.tvGreeting.text = getString(R.string.greeting, state.teacherName)

                    // Courses list
                    binding.progressCourses.isVisible = state.isLoadingCourses
                    binding.rvCourses.isVisible       = !state.isLoadingCourses && state.errorMessage == null
                    binding.layoutError.isVisible     = state.errorMessage != null && !state.isLoadingCourses
                    binding.tvEmpty.isVisible         = !state.isLoadingCourses && state.courses.isEmpty() && state.errorMessage == null
                    coursesAdapter.submitList(state.courses)

                    // Sync status
                    val hasPending = state.pendingSyncCount > 0
                    binding.cardSync.isVisible     = hasPending || state.isSyncing
                    binding.tvSyncStatus.text      = if (hasPending)
                        getString(R.string.pending_sync, state.pendingSyncCount)
                    else getString(R.string.all_synced)
                    binding.btnSync.isEnabled      = !state.isSyncing && hasPending
                    binding.progressSync.isVisible = state.isSyncing
                    binding.progressSync.progress  = state.syncProgress

                    // Toast
                    state.toastMessage?.let { msg ->
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        viewModel.onToastShown()
                    }
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
