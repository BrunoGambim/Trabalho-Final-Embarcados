package com.example.embarcados.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface  UserDAO {
    @Query("SELECT * FROM user where uid = 1")
    fun getUser(): List<UserEntity>
    @Insert
    fun insert(vararg users: UserEntity)
    @Update
    fun updateUsers(vararg users: UserEntity)
}