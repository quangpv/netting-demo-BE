package com.onehypernet.demo.model.response

data class ListResponse<T>(
    val data: List<T>,
    val metadata: MetadataResponse
) {
    companion object {
        fun <T> empty(): ListResponse<T> {
            return ListResponse(emptyList(), MetadataResponse(0, 0))
        }
    }
}

data class MetadataResponse(
    val page: Int,
    val total: Int,
)