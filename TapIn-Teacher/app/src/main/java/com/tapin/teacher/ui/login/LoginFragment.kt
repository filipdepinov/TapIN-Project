package com.tapin.teacher.ui.login

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
import com.tapin.teacher.R
import com.tapin.teacher.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text?.toString().orEmpty().trim()
            val password = binding.etPassword.text?.toString().orEmpty()
            binding.tilEmail.error    = null
            binding.tilPassword.error = null
            binding.tvError.isVisible = false

            var valid = true
            if (email.isBlank()) { binding.tilEmail.error = "Email required"; valid = false }
            if (password.isBlank()) { binding.tilPassword.error = "Password required"; valid = false }
            if (valid) viewModel.login(email, password)
        }

        binding.etPassword.setOnEditorActionListener { _, _, _ ->
            binding.btnLogin.performClick(); true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressLogin.isVisible = state is LoginUiState.Loading
                    binding.btnLogin.isEnabled      = state !is LoginUiState.Loading
                    when (state) {
                        is LoginUiState.Success -> findNavController().navigate(R.id.action_login_to_home)
                        is LoginUiState.Error   -> {
                            binding.tvError.text      = state.message
                            binding.tvError.isVisible = true
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
