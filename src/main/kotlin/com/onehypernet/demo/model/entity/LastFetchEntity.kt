package com.onehypernet.demo.model.entity

import org.springframework.data.keyvalue.annotation.KeySpace

@KeySpace("last_fetch")
class LastFetchEntity(
    val lastFetch: Long
)