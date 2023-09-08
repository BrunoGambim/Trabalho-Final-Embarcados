package com.example.embarcados.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.embarcados.data.network.Esp8266Gateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRFIDViewModel : ViewModel() {
    fun addRFID(rfidName: String, boardName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Esp8266Gateway.INSTANCE.sendAddRFIDPackage(rfidName, boardName)
        }
    }
}