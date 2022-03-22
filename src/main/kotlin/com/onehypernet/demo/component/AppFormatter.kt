package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Component
class AppFormatter {
    companion object {
        const val UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    }

    fun formatDate(date: LocalDateTime): String {
        return DateTimeFormatter
            .ofPattern(UTC_PATTERN)
            .format(date)
    }

    fun formatMonth(date: LocalDate): String {
        return DateTimeFormatter.ofPattern("yyyy-MM").format(date)
    }

    fun formatRequestDate(date: LocalDateTime): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
    }

    fun formatAmount(amount: BigDecimal): BigDecimal {
        return amount.setScale(2, RoundingMode.HALF_UP)
    }

    fun formatPercent(percent: Double): Double {
        return DecimalFormat("#0.00").format(percent).toDouble()
    }

    fun formatTimeAgo(fromDateTime: LocalDateTime): String {
        val toDateTime = LocalDateTime.now()

        var tempDateTime = LocalDateTime.from(fromDateTime)

        val years = tempDateTime.until(toDateTime, ChronoUnit.YEARS)
        tempDateTime = tempDateTime.plusYears(years)

        val months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS)
        tempDateTime = tempDateTime.plusMonths(months)

        val days = tempDateTime.until(toDateTime, ChronoUnit.DAYS)
        tempDateTime = tempDateTime.plusDays(days)

        val hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS)
        tempDateTime = tempDateTime.plusHours(hours)

        val minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES)
        tempDateTime = tempDateTime.plusMinutes(minutes)

        val seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS)
        val builder = StringBuilder()

        fun append(suffix: String, number: Long): Boolean {
            if (number > 0) {
                builder.append(" ").append(number).append(" $suffix")
                return true
            }
            return false
        }

        var isAppended = append("years", years)
        isAppended = isAppended || append("months", months)
        isAppended = isAppended || append("days", days)
        isAppended = isAppended || append("hours", hours)
        isAppended = isAppended || append("minutes", minutes)
//        append("seconds", seconds)
        if (!isAppended) return "Just now"
        return builder.trim().toString()
    }

    fun formatTimeAgo(fromDateTime: String?): String {
        fromDateTime ?: return "Jut now"
        val dateTime = LocalDateTime.parse(fromDateTime, DateTimeFormatter.ofPattern(UTC_PATTERN))
        return formatTimeAgo(dateTime)
    }
}