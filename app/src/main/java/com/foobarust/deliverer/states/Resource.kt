package com.foobarust.deliverer.states

/**
 * Created by kevin on 2/17/21
 */

/**
 * Created by kevin on 8/9/20
 */
sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()

    data class Error(val message: String?) : Resource<Nothing>()

    data class Loading(val progress: Double? = null) : Resource<Nothing>()
}

/**
 * Check if there is a correctly received data,
 * or else return a custom fallback object
 */
fun <T> Resource<T>.getSuccessDataOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.data ?: fallback
}