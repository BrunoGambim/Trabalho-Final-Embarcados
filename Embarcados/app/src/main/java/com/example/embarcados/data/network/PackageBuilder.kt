package com.example.embarcados.data.network

import com.example.embarcados.models.Board
import com.example.embarcados.models.User
import java.lang.Exception

class PackageBuilder {
    companion object{
        private const val SEP = 0.toChar()
        private const val FIND_BOARD_PACKAGE = 1.toChar()
        private const val ADD_BOARD_PACKAGE = 2.toChar()
        private const val OPEN_PACKAGE = 3.toChar()
        private const val CLOSE_PACKAGE = 4.toChar()
        private const val REMOVE_PACKAGE = 5.toChar()
        private const val GIVE_ACCESS_PACKAGE = 6.toChar()
        private const val ADD_RFID_PACKAGE = 7.toChar()

        fun buildFindBoardPackage(id: String, name:String):String{
            return "$FIND_BOARD_PACKAGE$SEP$id$SEP$name$SEP"
        }

        fun buildOpenPackage(id: String, name:String):String{
            return "$OPEN_PACKAGE$SEP$id$SEP$name$SEP"
        }

        fun buildClosePackage(id: String, name:String):String{
            return "$CLOSE_PACKAGE$SEP$id$SEP$name$SEP"
        }

        fun buildRemovePackage(id: String, boardName: String, username:String):String{
            return "$REMOVE_PACKAGE$SEP$id$SEP$boardName$SEP$username$SEP"
        }

        fun buildGiveAccessPackage(id: String, boardName: String, username:String):String{
            return "$GIVE_ACCESS_PACKAGE$SEP$id$SEP$boardName$SEP$username$SEP"
        }
        fun buildAddRfidPackage(id: String, boardName: String, rfidName:String):String{
            return "$ADD_RFID_PACKAGE$SEP$id$SEP$boardName$SEP$rfidName$SEP"
        }


        fun buildAddBoardPackage(
            boardName: String,
            wifiName: String,
            wifiPassword: String,
            userID: String,
            userName: String
        ): String{
            return "$ADD_BOARD_PACKAGE$SEP$boardName$SEP$wifiName$SEP$wifiPassword$SEP$userID$SEP$userName$SEP"
        }

        fun decodeBoardPackage(pkg: String): Board? {
            try {
                val components = pkg.split(0.toChar())
                println("components")
                components.forEach{
                    println(it)
                }
                var hasAccess = false
                if(components[0].equals("1")){
                    hasAccess = true
                }else if(!components[0].equals("0")){
                    return null
                }
                var boardName = components[1];
                var isAdmin = components[2].isNotEmpty();
                var userList = mutableListOf<User>()
                if(components[2].isNotEmpty()){
                    for(i in 0 until components[2].toInt()){
                        userList.add(User(components[4 + 2*i], "", components[3 + 2*i].equals("1")))
                    }
                }
                return Board(boardName, userList, hasAccess, isAdmin)
            } catch (e: Exception){ }
            return null
        }
    }
}