package com.onehypernet.demo.component

import com.onehypernet.demo.extension.throws
import org.springframework.stereotype.Component
import java.text.DecimalFormat
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

    private val decimalFormat = DecimalFormat("00")

    fun nowStr(): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(nowDate())
    }

    fun dateOf(date: String): LocalDateTime {
        return try {
            DateTimeFormatter.ofPattern(DATE_PATTERN).let { LocalDate.parse(date, it).atStartOfDay() }
        } catch (e: Throwable) {
            try {
                var (firstSeg, secondSeg, lastSeg) = date.split("-")
                if (firstSeg.length < 2) {
                    firstSeg = decimalFormat.format(firstSeg.toIntOrNull())
                }
                if (secondSeg.length < 2) {
                    secondSeg = decimalFormat.format(secondSeg.toIntOrNull())
                }
                if (lastSeg.length < 2) {
                    lastSeg = decimalFormat.format(lastSeg.toIntOrNull())
                }

                val newDate = "$firstSeg-$secondSeg-$lastSeg"
                try {
                    DateTimeFormatter.ofPattern(DATE_PATTERN1).let { LocalDate.parse(newDate, it).atStartOfDay() }
                } catch (e1: Throwable) {
                    DateTimeFormatter.ofPattern(DATE_PATTERN2).let { LocalDate.parse(newDate, it).atStartOfDay() }
                }
            } catch (e: Throwable) {
                throws("Invalid format $date")
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