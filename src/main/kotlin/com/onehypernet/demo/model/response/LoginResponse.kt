package com.onehypernet.demo.model.response

data class LoginResponse(
    val accessToken: String,
    val email: String,
    val name: String
)