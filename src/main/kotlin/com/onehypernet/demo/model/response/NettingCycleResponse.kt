package com.onehypernet.demo.model.response

import com.onehypernet.demo.model.enumerate.NettingStatus

data class NettingCycleResponse(
    val id: String,
    val group: String,
    val status: NettingStatus,
    val receivable: Amount? = null,
    val payable: Amount? = null,
    val transactionCount: Int? = null,
    val savingFee: Amount? = null,
    val savingCash: Amount? = null,

    val createAt: String,
    val settlementDate: String? = null,
)