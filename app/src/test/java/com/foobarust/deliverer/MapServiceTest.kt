package com.foobarust.deliverer

import com.foobarust.deliverer.api.MapService
import com.foobarust.deliverer.api.deserializers.DirectionsDeserializer
import com.foobarust.deliverer.constants.Constants
import com.foobarust.deliverer.data.response.DirectionsResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kevin on 4/1/21
 */

class MapServiceTest {

    @Test
    fun test_map_service() = runBlocking {
        val gson = GsonBuilder().registerTypeAdapter(
            DirectionsResponse::class.java,
            DirectionsDeserializer()
        ).create()

        val mapService = Retrofit.Builder()
            .baseUrl(Constants.MAPS_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MapService::class.java)

        val response = mapService.getDirections(
            key = BuildConfig.GOOGLE_MAPS_API_KEY,
            origin = "22.3423071,114.1917143",
            destination = "22.336263,114.263532",
            travelMode = "driving"
        )

        println(response.toString())

        assert(true)
    }

    @Test
    fun test() = runBlocking {
        flowOf(1, 2, 3).onEach {
            delay(1000)
        }.collect {
            println(it)
        }
    }
}