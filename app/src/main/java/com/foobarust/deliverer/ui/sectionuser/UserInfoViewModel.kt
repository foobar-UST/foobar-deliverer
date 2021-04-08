package com.foobarust.deliverer.ui.sectionuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.UserDelivery
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 4/7/21
 */

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    val userProfile: StateFlow<Resource<UserDelivery>> = _userId
        .filterNotNull()
        .flatMapLatest { getUserInfoUseCase(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Resource.Loading()
        )

    fun onFetchUserProfile(userId: String) {
        _userId.value = userId
    }
}