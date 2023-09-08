package com.example.embarcados.data.database

import android.content.Context
import androidx.room.Room
import com.example.embarcados.models.User
import java.util.UUID

class UserRepository(private var context: Context) {
    private val dao = Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build().UserDAO()

    suspend fun getUser(): User{
        var user = dao.getUser()
        if(user.isEmpty()){
            var newUser = UserEntity(1,"Username", UUID.randomUUID().toString())
            dao.insert(newUser)
            return newUser.toModel()
        }else{
            return user[0].toModel()
        }
    }

    suspend fun updateUser(user: User){
        dao.updateUsers(UserEntity.fromModel(user))
    }

    companion object{
        lateinit var INSTANCE: UserRepository
    }
}