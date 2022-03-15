package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.LastFetchEntity
import org.springframework.data.keyvalue.core.KeyValueOperations
import org.springframework.stereotype.Component

@Component
class LastFetchCache(
    private val template: KeyValueOperations
) {
    operator fun get(key: String): Long {
        return template.findById(key, LastFetchEntity::class.java)
            .orElseGet { LastFetchEntity(0) }.lastFetch
    }

    fun markAsLastFetch(key: String): Long {
        val currentTime = System.currentTimeMillis()
        template.insert(key, LastFetchEntity(System.currentTimeMillis()))
        return currentTime
    }
}