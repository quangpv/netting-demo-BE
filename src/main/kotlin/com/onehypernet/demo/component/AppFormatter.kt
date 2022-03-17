package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class AppFormatter {
    fun formatDate(date: LocalDateTime): String {
        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
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

}