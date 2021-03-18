package com.foobarust.deliverer.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentAuthInputBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthInputFragment : Fragment() {

    private var binding: FragmentAuthInputBinding by AutoClearedValue(this)
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthInputBinding.inflate(inflater, container, false)

        // Input login email
        binding.emailEditText.doOnTextChanged { text, _, _, _ ->
            authViewModel.onEmailAddressUpdated(text.toString())
        }

        // Input login password
        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            authViewModel.onPasswordUpdated(text.toString())
        }

        // Sign-in button
        binding.confirmButton.setOnClickListener {
            authViewModel.onSignIn()
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState == AuthUiState.VERIFYING
                    confirmButton.isVisible = uiState == AuthUiState.INPUT
                }

                if (uiState == AuthUiState.VERIFYING) {
                    findNavController(R.id.authInputFragment)?.navigate(
                        AuthInputFragmentDirections.actionAuthInputFragmentToAuthVerifyFragment()
                    )
                }
            }
        }

        return binding.root
    }
}