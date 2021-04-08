package com.foobarust.deliverer.usecases.section

import com.foobarust.deliverer.di.DefaultDispatcher
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

/**
 * Countdown timer
 * Input: period in milliseconds
 * Output: is the timer active
 *
 * Created by kevin on 1/22/21
 */

class PeriodicTimerUseCase @Inject constructor(
    @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Long, Unit>(coroutineDispatcher) {

    override fun execute(parameters: Long): Flow<Resource<Unit>> = callbackFlow {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                channel.offer(Resource.Success(Unit))
            }
        }, 0 /* delay */,  parameters /* period */)

        awaitClose { timer.cancel() }
    }
}