package com.onehypernet.demo.model.response

import com.onehypernet.demo.model.enumerate.TransactionType


data class NettedTransactionResponse(
    val date: String,
    val dueDate: String,
    val transactionId: String,
    val type: TransactionType,
    val counterParty: String,
    val billAmount: Amount,
    val localAmount: Amount,
    val feeSaved: Amount
)