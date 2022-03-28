package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class AppCalendar {
    companion object {
        const val DATE_PATTERN = "yyyy-MM-dd"
        const val DATE_PATTERN1 = "dd-MM-yyyy"
        const val DATE_PATTERN2 = "dd-MM-yy"
    }

    fun nowStr(): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(nowDate())
    }

    fun dateOf(date: String): LocalDateTime {
        return try {
            DateTimeFormatter.ofPattern(DATE_PATTERN).let { LocalDate.parse(date, it).atStartOfDay() }
        } catch (e: Throwable) {
            try {
                DateTimeFormatter.ofPattern(DATE_PATTERN1).let { LocalDate.parse(date, it).atStartOfDay() }
            } catch (e1: Throwable) {
                DateTimeFormatter.ofPattern(DATE_PATTERN2).let { LocalDate.parse(date, it).atStartOfDay() }
            }
        }
    }

    fun getPreviousMonths(amount: Int, skip: Int = 1): List<LocalDate> {
        val now = LocalDate.now(ZoneId.of("UTC")).withDayOfMonth(1)
        return (1..amount).map { now.plusMonths(-(it + skip).toLong()) }.reversed()
    }

    fun nowDate(): LocalDate {
        return LocalDate.now(ZoneId.of("UTC"))
    }

    fun nowMonth(): LocalDate {
        return LocalDate.now(ZoneId.of("UTC")).withDayOfMonth(1)
    }

}