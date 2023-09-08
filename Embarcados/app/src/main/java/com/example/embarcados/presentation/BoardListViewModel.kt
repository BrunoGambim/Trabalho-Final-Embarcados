package com.example.embarcados.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.embarcados.data.network.Esp8266Gateway
import com.example.embarcados.models.Board
import com.example.embarcados.presentation.view_state.BoardCardViewState
import com.example.embarcados.presentation.view_state.BoardListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BoardListViewModel : ViewModel() {
    private val _viewState = MutableLiveData<BoardListViewState>()
    val viewState : LiveData<BoardListViewState> get() = _viewState
    private var job: Job? = null

    fun loadBoardList() {
        if(job == null || !job!!.isActive){
            getBoardList()
        }
        refreshList()
    }

    fun refreshList(){
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.postValue(BoardListViewState.Content(listOf()))
            Esp8266Gateway.INSTANCE.refreshList()
        }
    }

    private fun getBoardList(){
        job = viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.getBoardList(fun(list: List<Board>) : Unit {
                _viewState.postValue(
                    BoardListViewState.Content(list.map { BoardCardViewState.fromModel(it) })
                )
            })
        }
    }

    fun sendOpenPackage(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendOpenPackage(id)
        }
    }

    fun sendClosePackage(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendClosePackage(id)
        }
    }
}