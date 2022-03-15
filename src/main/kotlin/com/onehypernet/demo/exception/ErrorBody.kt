package com.onehypernet.demo.exception

data class ErrorBody(
    val message: String,
    val payload: Any? = null
)