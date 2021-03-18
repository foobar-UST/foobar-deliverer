package com.foobarust.deliverer.di

import com.foobarust.deliverer.data.mappers.OrderMapper
import com.foobarust.deliverer.repositories.OrderRepository
import com.foobarust.deliverer.repositories.OrderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 3/9/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule  {

    @Singleton
    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    companion object {
        @Singleton
        @Provides
        fun provideOrderMapper(): OrderMapper = OrderMapper()
    }
}