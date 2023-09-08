package com.example.embarcados.presentation.view_state

sealed class BoardListViewState {
    data class Content(val boardList: List<BoardCardViewState>) : BoardListViewState()
}