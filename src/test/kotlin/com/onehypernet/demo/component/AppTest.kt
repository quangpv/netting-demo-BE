package com.onehypernet.demo.component

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppTest {
    @Test
    fun test() {
        val fromDateTime = "2022-03-22T12:10:40Z"
        val dateTime = try {
            LocalDateTime.parse(fromDateTime, DateTimeFormatter.ofPattern(AppFormatter.UTC_PATTERN))
        } catch (e: Throwable) {
            LocalDateTime.parse(fromDateTime, DateTimeFormatter.ofPattern(AppFormatter.UTC_PATTERN1))
        }
        println(dateTime)
    }

    @Test
    fun test1() {
        var date = AppCalendar().dateOf("1-1-2022")
        println(date)
        date = AppCalendar().dateOf("01-1-2022")
        println(date)
        date = AppCalendar().dateOf("01-10-22")
        println(date)
    }
}