package com.example.embarcados.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.embarcados.data.network.Esp8266Gateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBoardViewModel : ViewModel() {
    fun addBoard(boardName: String, wifiName: String, wifiPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendAddBoardPackage(boardName, wifiName, wifiPassword)
        }
    }
}