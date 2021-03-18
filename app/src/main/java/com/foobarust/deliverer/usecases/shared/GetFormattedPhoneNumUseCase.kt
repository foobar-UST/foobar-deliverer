package com.foobarust.deliverer.usecases.shared

import com.foobarust.deliverer.di.MainDispatcher
import com.foobarust.deliverer.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetFormattedPhoneNumUseCase @Inject constructor(
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<String, String>(coroutineDispatcher) {

    override suspend fun execute(parameters: String): String {
        if (parameters.length != LENGTH) {
            throw Exception("Invalid phone number.")
        }

        return "$AREA_CODE $parameters"
    }

    companion object {
        const val LENGTH = 8
        const val AREA_CODE = "+852"
    }
}