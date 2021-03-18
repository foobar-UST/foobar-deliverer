package com.foobarust.deliverer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foobarust.deliverer.data.dtos.UserDetailCacheDto

/**
 * Created by kevin on 2/17/21
 */

@Database(
    entities = [UserDetailCacheDto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDetailDto(): UserDetailDao
}