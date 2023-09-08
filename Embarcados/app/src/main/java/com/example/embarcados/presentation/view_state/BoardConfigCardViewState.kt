package com.example.embarcados.presentation.view_state

import com.example.embarcados.models.User

data class BoardConfigCardViewState(var name: String, var hasAccess: Boolean){
    companion object {
        fun fromModel(user: User) : BoardConfigCardViewState {
            return BoardConfigCardViewState(user.name, user.hasAccess)
        }
    }
}