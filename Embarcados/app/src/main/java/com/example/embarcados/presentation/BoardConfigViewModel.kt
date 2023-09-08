package com.example.embarcados.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.embarcados.data.network.Esp8266Gateway
import com.example.embarcados.presentation.view_state.BoardConfigCardViewState
import com.example.embarcados.presentation.view_state.BoardConfigViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardConfigViewModel : ViewModel() {
    private val _viewState = MutableLiveData<BoardConfigViewState>()
    val viewState : LiveData<BoardConfigViewState> get() = _viewState

    fun loadBoardList(id: String) {
        println("load board list dispatch")
        viewModelScope.launch(Dispatchers.IO) {
            var list = Esp8266Gateway.INSTANCE.getBoardConfig(id)
            println("load board list")
            list.forEach{
                print(it.name)
            }
            _viewState.postValue(
                BoardConfigViewState.Content(list.map { BoardConfigCardViewState.fromModel(it) })
            )
        }
    }

    fun giveAccess(boardName: String, username: String){
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendGiveAccessPackage(boardName, username)
        }
    }

    fun removeUser(boardName: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendRemoveUserPackage(boardName, username)
        }
    }
}