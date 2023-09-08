package com.example.embarcados.models

data class Board(var name: String, var users: List<User>, var hasAccess: Boolean, var isAdmin: Boolean)