package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.util.*

@Component
class NettingIdGenerator {
    private var mLastTxId = System.currentTimeMillis()

    fun generate(): String {
        return doGenerate("NT")
    }

    private fun doGenerate(prefix: String): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val milliSecond = calendar.get(Calendar.MILLISECOND)
        val formatter = DecimalFormat("00")

        return StringBuilder()
            .append(prefix)
            .append(formatter.format(year % 100))
            .append(formatter.format(month))
            .append(formatter.format(date))
            .append(formatter.format(hour))
            .append(formatter.format(min))
            .append(formatter.format(second))
            .append(formatter.format(milliSecond % 100))
            .toString()
    }

    fun getPartyId(name: String): String {
        return toSnakeCase(name)
    }

    private fun toSnakeCase(str: String): String {
        val n = str.length
        var str1 = ""
        for (i in 0 until n) {
            str1 = if (str[i] == ' ') str1 + '_' else str1 + Character.toUpperCase(str[i])
        }
        return str1
    }

    fun generateTxId(): String {
        return "TX${mLastTxId}".also { mLastTxId++ }
    }
}