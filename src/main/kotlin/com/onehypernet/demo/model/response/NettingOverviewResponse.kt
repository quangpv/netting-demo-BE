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
    var savedInMonth: BigDecimal = BigDecimal(0.0),
    var savedYTD: BigDecimal = BigDecimal(0.0),
    var savedList: ArrayList<SavedByMonthResponse> = ArrayList()
)

class SavedByMonthResponse(
    val month: String,
    val amount: BigDecimal
)