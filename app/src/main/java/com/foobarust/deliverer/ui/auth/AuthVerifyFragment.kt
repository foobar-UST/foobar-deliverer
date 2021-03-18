package com.foobarust.deliverer.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentAuthVerifyBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthVerifyFragment : Fragment() {

    private var binding: FragmentAuthVerifyBinding by AutoClearedValue(this)
    private val authViewModel: AuthViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            authViewModel.onSignInCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // When back pressed, cancel the email verification and
        // navigate back to input screen
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthVerifyBinding.inflate(inflater, container, false)

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authUiState.collect { uiState ->
                if (uiState == AuthUiState.INPUT) {
                    findNavController(R.id.authVerifyFragment)?.navigate(
                        AuthVerifyFragmentDirections.actionAuthVerifyFragmentToAuthInputFragment()
                    )
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        backPressedCallback.remove()
        super.onDestroy()
    }
}