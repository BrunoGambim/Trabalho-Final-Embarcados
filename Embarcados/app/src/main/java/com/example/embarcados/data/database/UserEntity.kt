package com.example.embarcados.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.embarcados.models.User

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "username") val username: String?,
    val id: String
){
    fun toModel(): User{
        return User(this.username ?: "", this.id, false)
    }

    companion object{
        fun fromModel(user: User): UserEntity {
            return UserEntity(1, user.name, user.id)
        }
    }
}