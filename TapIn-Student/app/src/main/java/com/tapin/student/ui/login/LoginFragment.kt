package com.tapin.student.ui.login

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
import com.tapin.student.R
import com.tapin.student.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeUiState()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            // Clear previous errors
            binding.tilEmail.error    = null
            binding.tilPassword.error = null
            binding.tvError.isVisible = false

            // Client-side validation
            var valid = true
            if (email.isBlank()) {
                binding.tilEmail.error = getString(R.string.error_empty_email)
                valid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                valid = false
            }
            if (password.isBlank()) {
                binding.tilPassword.error = getString(R.string.error_empty_password)
                valid = false
            }
            if (valid) viewModel.login(email, password)
        }

        // Allow pressing Done on keyboard to trigger login
        binding.etPassword.setOnEditorActionListener { _, _, _ ->
            binding.btnLogin.performClick()
            true
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUiState.Idle -> {
                            binding.progressLogin.isVisible = false
                            binding.btnLogin.isEnabled = true
                        }
                        is LoginUiState.Loading -> {
                            binding.progressLogin.isVisible = true
                            binding.btnLogin.isEnabled = false
                            binding.tvError.isVisible = false
                        }
                        is LoginUiState.Success -> {
                            binding.progressLogin.isVisible = false
                            findNavController().navigate(R.id.action_login_to_home)
                        }
                        is LoginUiState.Error -> {
                            binding.progressLogin.isVisible = false
                            binding.btnLogin.isEnabled = true
                            binding.tvError.text = state.message
                            binding.tvError.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
