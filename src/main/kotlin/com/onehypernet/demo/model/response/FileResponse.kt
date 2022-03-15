package com.onehypernet.demo.model.response
data class FileResponse(
    val name: String,
    val size: Long,
    val extension: String,
    val path: String
)