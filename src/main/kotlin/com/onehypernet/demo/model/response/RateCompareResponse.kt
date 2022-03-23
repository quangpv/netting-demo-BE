package com.onehypernet.demo.model.response

import java.math.BigDecimal

data class RateCompareResponse(
    val exchangeRate: BigDecimal,
    val lastTime: String,
    val invoiceCurrency: String,
    val homeCurrency: String,
    val compares: List<CompareItemResponse>
)

data class CompareItemResponse(
    val logo: String,
    val name: String,
    val exchangeRate: BigDecimal,
    val transferFee: BigDecimal,
    val totalPayment: BigDecimal,
    val loss: BigDecimal,
)