package com.foobarust.deliverer.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.ui.settings.SettingsListModel.SettingsAccountItemModel
import com.foobarust.deliverer.ui.settings.SettingsListModel.SettingsSectionItemModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

const val SETTINGS_EMPLOYED_BY = "settings_employed_by"
const val SETTINGS_CONTACT_US = "settings_contact_us"
const val SETTINGS_TERMS_CONDITIONS = "settings_terms_conditions"
const val SETTINGS_SIGN_OUT = "settings_sign_out"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase
) : ViewModel() {

    private val _settingsListModels = MutableStateFlow<List<SettingsListModel>>(emptyList())
    val settingsListModels: StateFlow<List<SettingsListModel>> = _settingsListModels.asStateFlow()

    private val _settingsUiState = MutableStateFlow(SettingsUiState.LOADING)
    val settingsUiState: LiveData<SettingsUiState> = _settingsUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    init {
        // Observe auth state
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect {
                when (it) {
                    is AuthState.Authenticated -> {
                        _settingsListModels.value = buildAuthenticatedListModels(it.data)
                        _settingsUiState.value = SettingsUiState.COMPLETED
                    }
                    AuthState.Unauthenticated, AuthState.Unverified -> {
                        _settingsListModels.value = buildCommonListModels()
                        _settingsUiState.value = SettingsUiState.COMPLETED
                    }
                    AuthState.Loading -> {
                        _settingsUiState.value = SettingsUiState.LOADING
                    }
                }
            }
        }
    }

    fun onShowSnackBarMessage(message: String) {
        _snackBarMessage.offer(message)
    }

    private fun buildAuthenticatedListModels(
        userDetail: UserDetail
    ): List<SettingsListModel> = buildList {
        add(SettingsAccountItemModel(
            username = userDetail.username,
            photoUrl = userDetail.photoUrl
        ))

        add(SettingsSectionItemModel(
            id = SETTINGS_EMPLOYED_BY,
            drawableRes = R.drawable.ic_badge,
            title = context.getString(R.string.settings_section_employed_by)
        ))

        addAll(buildCommonListModels())

        add(SettingsSectionItemModel(
            id = SETTINGS_SIGN_OUT,
            drawableRes = R.drawable.ic_exit_to_app,
            title = context.getString(R.string.settings_section_sign_out_title)
        ))
    }

    private fun buildCommonListModels(): List<SettingsListModel> = buildList {
        addAll(listOf(
            SettingsSectionItemModel(
                id = SETTINGS_CONTACT_US,
                drawableRes = R.drawable.ic_live_help,
                title = context.getString(R.string.settings_section_contact_us_title)
            ),
            SettingsSectionItemModel(
                id = SETTINGS_TERMS_CONDITIONS,
                drawableRes = R.drawable.ic_copyright,
                title = context.getString(R.string.settings_section_license_title)
            )
        ))
    }
}

enum class SettingsUiState {
    COMPLETED,
    LOADING
}