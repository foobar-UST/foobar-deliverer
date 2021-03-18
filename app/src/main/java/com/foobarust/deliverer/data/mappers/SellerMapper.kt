package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.data.dtos.SellerDetailDto
import com.foobarust.deliverer.data.dtos.SellerRatingCountDto
import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.data.models.SellerRatingCount
import com.foobarust.deliverer.data.models.SellerType
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

    private fun mapSellerRatingCount(dto: SellerRatingCountDto): SellerRatingCount {
        return SellerRatingCount(
            excellent = dto.excellent ?: 0,
            veryGood = dto.veryGood ?: 0,
            good = dto.good ?: 0,
            fair = dto.fair ?: 0,
            poor = dto.poor ?: 0
        )
    }
}