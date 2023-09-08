package com.example.embarcados.presentation.view_state

import com.example.embarcados.models.Board

data class BoardCardViewState(var name: String, var hasAccess: Boolean, var isAdmin: Boolean){
    companion object {
        fun fromModel(board: Board) : BoardCardViewState {
            return BoardCardViewState(board.name, board.hasAccess, board.isAdmin)
        }
    }
}
