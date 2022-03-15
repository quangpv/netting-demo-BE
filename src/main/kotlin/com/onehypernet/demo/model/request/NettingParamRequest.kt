package com.onehypernet.demo.model.request

data class NettingParamRequest(
    val from: String = "",
    val to: String = "",
    val location: String = "",
    val destinations: String = "",
)