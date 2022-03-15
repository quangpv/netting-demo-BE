package com.onehypernet.demo.model.response

import com.onehypernet.demo.model.enumerate.NettingStatus
import java.math.BigDecimal

data class NettingCycleDetailResponse(
    val id: String,
    val group: String,
    val status: NettingStatus,
    val currency: String? = null,

    val createAt: String,
    val settlementDate: String? = null,

    val uploadedFile: FileResponse? = null,
    val reportFile: FileResponse? = null,

    val receivable: NettedReportResponse? = null,
    val payable: NettedReportResponse? = null,
    val netCashFlow: NettedReportResponse? = null,

    val savingFee: EstimatedSavingResponse? = null,
    val savingCash: EstimatedSavingResponse? = null,
    val potential: Double? = null,

    val summary: SummaryReportResponse? = null,
    val nettedTransactions: List<NettedTransactionResponse> = emptyList()

)

data class NettedReportResponse(
    val amount: BigDecimal = BigDecimal(0.0),
    val numOfTransactions: Int = 0,
    val numOfCounterParties: Int = 0
)

data class EstimatedSavingResponse(
    val before: BigDecimal = BigDecimal.valueOf(0.0),
    val after: BigDecimal = BigDecimal.valueOf(0.0),
    val savingAmount: BigDecimal = BigDecimal.valueOf(0.0),
    val savingPercent: Double = 0.0,
)

data class BeforeAfterResponse(
    val before: BigDecimal = BigDecimal.valueOf(0.0),
    val after: BigDecimal = BigDecimal.valueOf(0.0)
)

data class SummaryReportResponse(
    val transactions: BeforeAfterResponse = BeforeAfterResponse(),
    val currencies: BeforeAfterResponse = BeforeAfterResponse(),
    val fees: BeforeAfterResponse = BeforeAfterResponse(),
    val cashOutFlow: BeforeAfterResponse = BeforeAfterResponse(),
)