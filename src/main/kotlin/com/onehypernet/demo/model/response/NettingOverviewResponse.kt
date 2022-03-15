package com.onehypernet.demo.model.response

import java.math.BigDecimal

data class NettingOverviewResponse(
    val month: String,
    val currency: String,
    val receivable: BigDecimal,
    val payable: BigDecimal,
    val cashFlow: BigDecimal,
    val feeSaved: SavedOverviewResponse,
    val cashFlowSaved: SavedOverviewResponse,
)

class SavedOverviewResponse(
    val savedInMonth: BigDecimal,
    val savedYTD: BigDecimal,
    val savedList: List<SavedByMonthResponse>
)

class SavedByMonthResponse(
    val month: String,
    val amount: BigDecimal
)