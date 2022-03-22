package com.onehypernet.demo.model.response

import java.math.BigDecimal

data class RateCompareResponse(
    val exchangeRate: Double,
    val lastTime: String,
    val invoiceCurrency: String,
    val homeCurrency: String,
    val compares: List<CompareItemResponse>
)

data class CompareItemResponse(
    val logo: String,
    val exchangeRate: Double,
    val transferFee: BigDecimal,
    val totalPayment: BigDecimal,
    val loss: BigDecimal,
)