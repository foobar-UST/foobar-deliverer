package com.foobarust.deliverer.di

import com.foobarust.deliverer.data.mappers.UserMapper
import com.foobarust.deliverer.db.AppDatabase
import com.foobarust.deliverer.db.UserDetailDao
import com.foobarust.deliverer.repositories.UserRepository
import com.foobarust.deliverer.repositories.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 2/17/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Singleton
    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    companion object {
        @Singleton
        @Provides
        fun provideUserDetailDao(appDatabase: AppDatabase): UserDetailDao {
            return appDatabase.userDetailDto()
        }

        @Singleton
        @Provides
        fun provideUserMapper(): UserMapper = UserMapper()
    }
}