package com.foobarust.deliverer.usecases.section

import com.foobarust.deliverer.data.models.SellerSectionDetail
import org.threeten.bp.Duration
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 3/22/21
 */

class GetDeliveryTimeLeftUseCase @Inject constructor() {

    operator fun invoke(
        sectionDetail: SellerSectionDetail,
        hoursSuffix: String,
        minutesSuffix: String
    ): String {
        val deliveryTimeRemain = sectionDetail.deliveryTime.time - Date().time
        val duration = Duration.ofMillis(deliveryTimeRemain)
        val hoursRemain = duration.toHours()
        val minutesRemain = duration.minusHours(hoursRemain).toMinutes()

        return if (hoursRemain <= 0) {
            "$minutesRemain$minutesSuffix"
        } else {
            "$hoursRemain$hoursSuffix $minutesRemain$minutesSuffix"
        }
    }
}