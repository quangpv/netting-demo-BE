package com.onehypernet.demo.model.response

data class NettingReportResponse(
    val nettedTransactions: List<NettedTransactionResponse>,
    val uploadedFile: FileResponse,
    val reportFile: FileResponse
)
