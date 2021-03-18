package com.foobarust.deliverer.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signOutUseCase: SignOutUseCase
) : BaseViewModel() {

    private val _emailAddress = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private val _authUiState = MutableStateFlow(AuthUiState.INPUT)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _navigateToMain = Channel<Unit>()
    val navigateToMain: Flow<Unit> = _navigateToMain.receiveAsFlow()

    private val _startAuthNav = Channel<Unit>()
    val startAuthNav: Flow<Unit> = _startAuthNav.receiveAsFlow()

    init {
        // Observe auth state
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect {
                Log.d(TAG, "authstate: $it")

                when (it) {
                    is AuthState.Authenticated -> {
                        _navigateToMain.offer(Unit)
                    }
                    AuthState.Unauthenticated -> {
                        _authUiState.value = AuthUiState.INPUT
                        _startAuthNav.offer(Unit)
                    }
                    AuthState.Unverified -> {
                        _authUiState.value = AuthUiState.INPUT
                        _startAuthNav.offer(Unit)
                        signOutWhenNotVerified()
                    }
                    AuthState.Loading -> Unit
                }
            }
        }
    }

    fun onSignIn() = viewModelScope.launch {
        val emailAddress = _emailAddress.value
        val password = _password.value

        // Check if fields ar empty
        if (emailAddress.isBlank()) {
            showToastMessage(context.getString(R.string.auth_input_email_empty))
            return@launch
        }

        if (password.isBlank()) {
            showToastMessage(context.getString(R.string.auth_input_password_empty))
            return@launch
        }

        clearAuthInputFields()

        // Sign in with email and password
        val params = SignInWithEmailAndPasswordParameters(
            email = emailAddress,
            password = password
        )

        signInWithEmailAndPasswordUseCase(params).collect {
            when (it) {
                is Resource.Success -> Unit
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> _authUiState.value = AuthUiState.VERIFYING
            }
        }
    }

    fun onEmailAddressUpdated(email: String) {
        _emailAddress.value = email
    }

    fun onPasswordUpdated(password: String) {
        _password.value = password
    }

    fun onSignInCancelled() = viewModelScope.launch {
        _authUiState.value = AuthUiState.INPUT
    }

    private fun signOutWhenNotVerified() = viewModelScope.launch {
        signOutUseCase(Unit).collect {
            when (it) {
                is Resource.Success -> Unit
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> showToastMessage("Not verified. Signing out.")
            }
        }
    }

    private fun clearAuthInputFields() {
        _emailAddress.value = ""
        _password.value = ""
    }
}

enum class AuthUiState {
    INPUT,
    VERIFYING
}