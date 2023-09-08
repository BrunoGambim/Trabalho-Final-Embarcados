package com.example.embarcados.data.network

import com.example.embarcados.data.database.UserRepository
import com.example.embarcados.models.Board
import com.example.embarcados.models.User

class Esp8266Gateway(private val repository: UserRepository) {
    suspend fun getBoardList(
        updateBoardList: (List<Board>) -> Unit
    ) {
        while(true) {
            var board = PackageBuilder.decodeBoardPackage(SocketAdapter.INSTANCE.receiveMessage())
            if(board != null){
                boards[board.name] = board
                updateBoardList(boards.values.toList())
            }
        }
    }

    suspend fun refreshList() {
        boards = mutableMapOf()
        sendFindBoardPackage()
    }

    suspend fun sendFindBoardPackage(){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildFindBoardPackage(repository.getUser().id,repository.getUser().name))
    }

    suspend fun sendAddBoardPackage(boardName: String, wifiName: String, wifiPassword: String) {
        HTTPAdapter.INSTANCE.sendPOSTMessage(PackageBuilder.buildAddBoardPackage(boardName, wifiName, wifiPassword,
            repository.getUser().id, repository.getUser().name))
    }

    suspend fun sendAddRFIDPackage(name: String, boardName: String){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildAddRfidPackage(repository.getUser().id, boardName, name))
    }

    suspend fun sendGiveAccessPackage(boardName: String, username: String){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildGiveAccessPackage(repository.getUser().id, boardName, username))
    }

    suspend fun sendRemoveUserPackage(boardName: String, username: String){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildRemovePackage(repository.getUser().id, boardName, username))
    }

    suspend fun sendOpenPackage(name: String){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildOpenPackage(repository.getUser().id,name))
    }

    suspend fun sendClosePackage(name: String){
        SocketAdapter.INSTANCE.sendMessage(PackageBuilder.buildClosePackage(repository.getUser().id,name))
    }

    suspend fun getBoardConfig(name: String): List<User> {
        println("boards")
        boards.keys.forEach{
            println(it)
        }
        println("board name: $name")
        return boards[name]?.users?.map {
            User(it.name, it.id, it.hasAccess)
        }?.filter {
            it.name != repository.getUser().name
        } ?: listOf()
    }

    companion object{
        lateinit var INSTANCE: Esp8266Gateway
        private var boards: MutableMap<String,Board> = mutableMapOf()
    }
}