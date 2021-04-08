package com.foobarust.deliverer.ui.location

import android.content.Context
import androidx.lifecycle.ViewModel
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.TravelMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Created by kevin on 3/19/21
 */

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val deliveryModeDialogItems: List<DeliveryModeDialogItem> = buildList {
        TravelMode.values().map {
            val title = when (it) {
                TravelMode.DRIVING -> context.getString(R.string.travel_mode_driving)
                TravelMode.WALKING -> context.getString(R.string.travel_mode_walking)
            }
            DeliveryModeDialogItem(
                travelMode = it,
                deliveryModeTitle = title
            )
        }.let { addAll(it) }
    }

    private val _bottomSheetPeekHeight = MutableStateFlow<Int?>(null)
    val bottomSheetPeekHeight: StateFlow<Int?> = _bottomSheetPeekHeight.asStateFlow()

    fun onUpdateBottomSheetPeekHeight(peekHeight: Int) {
        _bottomSheetPeekHeight.value = peekHeight
    }
}

data class DeliveryModeDialogItem(
    val travelMode: TravelMode,
    val deliveryModeTitle: String
)
