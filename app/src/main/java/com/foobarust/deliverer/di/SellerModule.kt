package com.foobarust.deliverer.di

import com.foobarust.deliverer.data.mappers.SellerMapper
import com.foobarust.deliverer.repositories.SellerRepository
import com.foobarust.deliverer.repositories.SellerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 3/1/21
 */

@Module(includes = [PersistentModule::class])
@InstallIn(SingletonComponent::class)
abstract class SellerModule {

    @Singleton
    @Binds
    abstract fun bindsSellerRepository(
        sellerRepositoryImpl: SellerRepositoryImpl
    ): SellerRepository

    companion object {
        @Singleton
        @Provides
        fun provideSellerMapper(): SellerMapper = SellerMapper()
    }
}