package com.onehypernet.demo.model.response

import java.math.BigDecimal

data class Amount(
    val currency: String,
    val amount: BigDecimal
) {
    companion object {
        fun of(currency: String, amount: Double): Amount {
            return Amount(currency, amount.toBigDecimal())
        }
    }
}