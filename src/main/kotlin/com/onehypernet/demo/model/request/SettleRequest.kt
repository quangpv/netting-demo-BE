package com.onehypernet.demo.model.request

import java.math.BigDecimal

class SettleRequest(
    val amount: BigDecimal = BigDecimal(0.0)
)