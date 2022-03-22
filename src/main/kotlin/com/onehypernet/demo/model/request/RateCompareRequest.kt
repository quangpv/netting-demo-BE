package com.onehypernet.demo.model.request

import java.math.BigDecimal

class RateCompareRequest(
    val invoiceCurrency: String = "",
    val homeCurrency: String = "",
    val amount: BigDecimal = 0.0.toBigDecimal(),
)