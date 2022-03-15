package com.onehypernet.demo.model.request

import org.springframework.data.domain.PageRequest

class PagingRequest(
    val page: Int = 1,
    val size: Int = 30
) {
    fun toPageRequest(): PageRequest = PageRequest.of(
        maxOf(page, 1) - 1,
        maxOf(size, 1)
    )
}
