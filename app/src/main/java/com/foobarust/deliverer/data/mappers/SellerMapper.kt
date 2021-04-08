package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.constants.Constants.MAPS_DIRECTIONS_MODE_DRIVING
import com.foobarust.deliverer.constants.Constants.MAPS_DIRECTIONS_MODE_WALKING
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_AVAILABLE
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_DELIVERED
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_PREPARING
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_PROCESSING
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_READY_FOR_PICK_UP
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_SHIPPED
import com.foobarust.deliverer.data.dtos.SellerDetailDto
import com.foobarust.deliverer.data.dtos.SellerRatingCountDto
import com.foobarust.deliverer.data.dtos.SellerSectionBasicDto
import com.foobarust.deliverer.data.dtos.SellerSectionDetailDto
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.data.request.UpdateSectionLocationRequest
import javax.inject.Inject

/**
 * Created by kevin on 3/1/21
 */

class SellerMapper @Inject constructor() {

    fun toSellerDetail(dto: SellerDetailDto): SellerDetail {
        return SellerDetail(
            id = dto.id!!,
            name = dto.name!!,
            nameZh = dto.nameZh,
            description = dto.description,
            descriptionZh = dto.descriptionZh,
            website = dto.website,
            phoneNum = dto.phone_num ?: "",
            location = dto.location!!.toGeolocation(),
            imageUrl = dto.image_url,
            minSpend = dto.min_spend!!,
            orderRating = dto.orderRating ?: 0.0,
            deliveryRating = dto.deliveryRating,
            ratingCount = mapSellerRatingCount(dto.ratingCount!!),
            type = SellerType.values()[dto.type!!],
            online = dto.online ?: false,
            openingHours = dto.openingHours!!,
            notice = dto.notice,
            tags = dto.tags ?: emptyList()
        )
    }

    fun toSellerSectionDetail(dto: SellerSectionDetailDto): SellerSectionDetail {
        return SellerSectionDetail(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            groupId = dto.groupId!!,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            deliveryCost = dto.deliveryCost!!,
            deliveryTime = dto.deliveryTime!!.toDate(),
            deliveryLocation = dto.deliveryLocation!!.toGeolocation(),
            description = dto.description!!,
            descriptionZh = dto.descriptionZh,
            cutoffTime = dto.cutoffTime!!.toDate(),
            maxUsers = dto.maxUsers!!,
            joinedUsersCount = dto.joinedUsersCount ?: 0,
            joinedUsersIds = dto.joinedUsersIds ?: emptyList(),
            imageUrl = dto.imageUrl,
            state = mapSectionState(dto.state!!),
            available = dto.available ?: false
        )
    }

    fun toSellerSectionBasic(dto: SellerSectionBasicDto): SellerSectionBasic {
        return SellerSectionBasic(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            deliveryTime = dto.deliveryTime!!.toDate(),
            cutoffTime = dto.cutoffTime!!.toDate(),
            maxUsers = dto.maxUsers!!,
            joinedUsersCount = dto.joinedUsersCount ?: 0,
            imageUrl = dto.imageUrl,
            state = mapSectionState(dto.state!!),
            available = dto.available ?: false
        )
    }

    fun toUpdateSectionLocationRequest(
        sectionId: String,
        geolocationPoint: GeolocationPoint,
        travelMode: TravelMode
    ): UpdateSectionLocationRequest {
        return UpdateSectionLocationRequest(
            sectionId = sectionId,
            latitude = geolocationPoint.latitude,
            longitude = geolocationPoint.longitude,
            travelMode = fromTravelMode(travelMode)
        )
    }

    private fun mapSellerRatingCount(dto: SellerRatingCountDto): SellerRatingCount {
        return SellerRatingCount(
            excellent = dto.excellent ?: 0,
            veryGood = dto.veryGood ?: 0,
            good = dto.good ?: 0,
            fair = dto.fair ?: 0,
            poor = dto.poor ?: 0
        )
    }

    private fun mapSectionState(sectionState: String): SellerSectionState {
        return when (sectionState) {
            SELLER_SECTION_STATE_AVAILABLE -> SellerSectionState.AVAILABLE
            SELLER_SECTION_STATE_PROCESSING -> SellerSectionState.PROCESSING
            SELLER_SECTION_STATE_PREPARING -> SellerSectionState.PREPARING
            SELLER_SECTION_STATE_SHIPPED -> SellerSectionState.SHIPPED
            SELLER_SECTION_STATE_READY_FOR_PICK_UP -> SellerSectionState.READY_FOR_PICK_UP
            SELLER_SECTION_STATE_DELIVERED -> SellerSectionState.DELIVERED
            else -> throw IllegalArgumentException("Invalid SellerSectionState.")
        }
    }

    private fun fromTravelMode(travelMode: TravelMode): String {
        return when (travelMode) {
            TravelMode.DRIVING -> MAPS_DIRECTIONS_MODE_DRIVING
            TravelMode.WALKING -> MAPS_DIRECTIONS_MODE_WALKING
        }
    }

    private fun toTravelMode(travelMode: String): TravelMode {
        return when (travelMode) {
            MAPS_DIRECTIONS_MODE_DRIVING -> TravelMode.DRIVING
            MAPS_DIRECTIONS_MODE_WALKING -> TravelMode.WALKING
            else -> throw IllegalStateException("Unknown travel mode: $travelMode")
        }
    }
}