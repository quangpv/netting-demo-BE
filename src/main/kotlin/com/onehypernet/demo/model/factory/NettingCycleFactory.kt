package com.onehypernet.demo.model.factory

import com.onehypernet.demo.component.TextFormatter
import com.onehypernet.demo.helper.ReportCalculator
import com.onehypernet.demo.model.entity.NettedTransactionEntity
import com.onehypernet.demo.model.entity.NettingCycleEntity
import com.onehypernet.demo.model.enumerate.TransactionType
import com.onehypernet.demo.model.response.Amount
import com.onehypernet.demo.model.response.NettingCycleResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class NettingCycleFactory(private val textFormatter: TextFormatter) {
    fun create(
        entity: NettingCycleEntity
    ): NettingCycleResponse {
        return NettingCycleResponse(
            id = entity.id,
            group = entity.nettingGroup,
            status = entity.status,
            createAt = textFormatter.formatDate(entity.createAt),
        )
    }

    fun create(
        localCurrency:String,
        entity: NettingCycleEntity,
        transactions: List<NettedTransactionEntity>,
        calculator: ReportCalculator
    ): NettingCycleResponse {

        var receive = BigDecimal(0.0)
        var pay = BigDecimal(0.0)

        var feeSaving = BigDecimal(0.0)

        transactions.forEach {
            val localAmount = calculator.getLocalAmount(it.amount, it.currency)
            if (it.transactionType == TransactionType.Payable) {
                pay += localAmount
                feeSaving += calculator.getFeeAmount(it.amount, it.currency)
            } else {
                receive += localAmount
            }
        }

        return NettingCycleResponse(
            id = entity.id,
            group = entity.nettingGroup,
            status = entity.status,
            createAt = textFormatter.formatDate(entity.createAt),
            receivable = Amount(localCurrency, receive),
            payable = Amount(localCurrency, pay),
            transactionCount = transactions.size,
            savingFee = Amount(localCurrency, feeSaving),
            savingCash = Amount(localCurrency, receive - pay)
        )
    }
}