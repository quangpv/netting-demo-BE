package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class TextFormatter {
    fun formatDate(date: LocalDateTime): String {
        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .format(date)
    }

    fun formatMonth(date: LocalDate): String {
        return DateTimeFormatter.ofPattern("yyyy-MM").format(date)
    }

}