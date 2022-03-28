package com.onehypernet.demo.component

import org.springframework.stereotype.Component
import java.text.DecimalFormat

@Component
class NettingIdGenerator {
    companion object {
        const val NETTING = "NT"
    }

    private var mLastTxId = System.currentTimeMillis()

    fun generate(fromId: String?): String {
        val longId = fromId?.removePrefix(NETTING)?.toLongOrNull() ?: 0
        val formatter = DecimalFormat("00000000")
        return "$NETTING${formatter.format(longId + 1)}"
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