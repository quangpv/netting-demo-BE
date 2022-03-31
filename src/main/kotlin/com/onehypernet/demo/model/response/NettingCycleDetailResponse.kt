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

    val potentialPercent: Double? = null,

    val summary: SummaryReportResponse? = null,
    val nettedTransactions: List<NettedTransactionResponse> = emptyList()

)

data class NettedReportResponse(
    var amount: BigDecimal = BigDecimal(0.0),
    var numOfTransactions: Int = 0,
    var numOfCounterParties: Int = 0
)

data class EstimatedSavingResponse(
    var before: BigDecimal = BigDecimal.valueOf(0.0),
    /**
     * Randomise savings between 50 to 92% of before
     */
    var after: BigDecimal = BigDecimal.valueOf(0.0),
    var savingAmount: BigDecimal = BigDecimal.valueOf(0.0),
    var savingPercent: Double = 0.0,
)

data class BeforeAfterResponse(
    var before: BigDecimal = BigDecimal.valueOf(0.0),
    var after: BigDecimal = BigDecimal.valueOf(0.0)
)

data class SummaryReportResponse(
    var transactions: BeforeAfterResponse = BeforeAfterResponse(),
    var currencies: BeforeAfterResponse = BeforeAfterResponse(),
    var fees: BeforeAfterResponse = BeforeAfterResponse(),
    var cashOutFlow: BeforeAfterResponse = BeforeAfterResponse(),
)