package com.foobarust.deliverer.data.models

/**
 * Created by kevin on 3/8/21
 */

data class SellerRatingCount(
    val excellent: Int,
    val veryGood: Int,
    val good: Int,
    val fair: Int,
    val poor: Int
)

fun SellerRatingCount.sum(): Int {
    return excellent + veryGood + good + fair + poor
}