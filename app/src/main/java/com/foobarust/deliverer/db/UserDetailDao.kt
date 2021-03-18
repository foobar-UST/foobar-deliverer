package com.foobarust.deliverer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foobarust.deliverer.data.dtos.UserDetailCacheDto

/**
 * Created by kevin on 1/23/21
 */

@Dao
interface UserDetailDao {

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserDetail(userId: String): UserDetailCacheDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(dto: UserDetailCacheDto)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}