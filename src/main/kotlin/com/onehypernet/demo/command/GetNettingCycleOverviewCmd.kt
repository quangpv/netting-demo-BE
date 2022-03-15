package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppCalendar
import com.onehypernet.demo.component.TextFormatter
import com.onehypernet.demo.model.enumerate.TransactionType
import com.onehypernet.demo.model.response.NettingOverviewResponse
import com.onehypernet.demo.model.response.SavedByMonthResponse
import com.onehypernet.demo.model.response.SavedOverviewResponse
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class GetNettingCycleOverviewCmd(
    private val appCalendar: AppCalendar,
    private val textFormatter: TextFormatter,
    private val userRepository: UserRepository,
    private val nettedTransactionRepository: NettedTransactionRepository
) {
    operator fun invoke(userId: String): NettingOverviewResponse {
        val thisMonth = appCalendar.nowMonth()
        val thisMonthStr = textFormatter.formatMonth(thisMonth)
        val currency = userRepository.findById(userId).get().detail?.currency ?: AppConst.BRIDGING_CURRENCY
        val previousMonths = appCalendar.getPreviousMonths(3) + thisMonth

        val nettedByThisMonth = nettedTransactionRepository.findAllByMonthAndUser(thisMonthStr, userId)
        var receive = BigDecimal(0.0)
        var pay = BigDecimal(0.0)

        nettedByThisMonth.forEach {
            if (it.transactionType == TransactionType.Payable) {
                pay += it.amount
            } else {
                receive += it.amount
            }
        }

        val feeSavedList = previousMonths.map {
            SavedByMonthResponse(
                month = textFormatter.formatMonth(it),
                0.0.toBigDecimal()
            )
        }
        val cashFlowSavedList = previousMonths.map {
            SavedByMonthResponse(
                month = textFormatter.formatMonth(it),
                0.0.toBigDecimal()
            )
        }

        return NettingOverviewResponse(
            month = thisMonthStr,
            currency = currency,
            receivable = receive,
            payable = pay,
            cashFlow = receive - pay,
            feeSaved = SavedOverviewResponse(
                savedInMonth = 0.0.toBigDecimal(),
                savedYTD = 0.0.toBigDecimal(),
                savedList = feeSavedList,
            ),
            cashFlowSaved = SavedOverviewResponse(
                savedInMonth = 0.0.toBigDecimal(),
                savedYTD = 0.0.toBigDecimal(),
                savedList = cashFlowSavedList
            )
        )
    }
}