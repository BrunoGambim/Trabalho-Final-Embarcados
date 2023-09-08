package com.example.embarcados.presentation.view_state

sealed class BoardConfigViewState {
    data class Content(val config: List<BoardConfigCardViewState>) : BoardConfigViewState()
}