package com.foobarust.deliverer.ui.sectionuser

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 4/2/21
 */

@HiltViewModel
class SectionUserViewModel @Inject constructor() : ViewModel() {

    private val _currentDestination = MutableStateFlow(-1)

    private val _toolbarTitle = MutableStateFlow("")
    val toolbarTitle: StateFlow<String> = _toolbarTitle.asStateFlow()

    private val _backPressed = Channel<Unit>()
    val backPressed: Flow<Unit> = _backPressed.receiveAsFlow()

    fun onUpdateToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun onUpdateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun onBackPressed() {
        _backPressed.offer(Unit)
    }
}

@Parcelize
data class SectionUserProperty(
    val sectionId: String,
    val joinedUsersCount: Int,
    val maxUsersCount: Int
) : Parcelable