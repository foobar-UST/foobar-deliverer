package com.foobarust.deliverer.di

import com.foobarust.deliverer.api.MapService
import com.foobarust.deliverer.api.deserializers.DirectionsDeserializer
import com.foobarust.deliverer.constants.Constants.MAPS_API_URL
import com.foobarust.deliverer.data.response.DirectionsResponse
import com.foobarust.deliverer.repositories.MapRepository
import com.foobarust.deliverer.repositories.MapRepositoryImpl
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by kevin on 3/8/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Singleton
    @Binds
    abstract fun bindsMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ) : MapRepository

    companion object {
        @Singleton
        @Provides
        fun provideMapService(): MapService {
            val gson = GsonBuilder().registerTypeAdapter(
                DirectionsResponse::class.java,
                DirectionsDeserializer()
            ).create()

            return Retrofit.Builder()
                .baseUrl(MAPS_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MapService::class.java)
        }
    }
}