package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppCalendar
import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.model.response.NettingOverviewResponse
import com.onehypernet.demo.model.response.SavedByMonthResponse
import com.onehypernet.demo.model.response.SavedOverviewResponse
import com.onehypernet.demo.repository.NettingReportRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GetNettingCycleOverviewCmd(
    private val appCalendar: AppCalendar,
    private val appFormatter: AppFormatter,
    private val userRepository: UserRepository,
    private val nettingReportRepository: NettingReportRepository
) {
    operator fun invoke(userId: String): NettingOverviewResponse {
        val thisMonth = appCalendar.nowMonth()
        val thisMonthStr = appFormatter.formatMonth(thisMonth)
        val currency = userRepository.findById(userId).get().detail?.currency ?: AppConst.BRIDGING_CURRENCY
        val previousMonths = appCalendar.getPreviousMonths(7, 0) + thisMonth

        val reportByThisMonth = nettingReportRepository.findAllByMonthAndUser(thisMonthStr, userId)
        var receive = BigDecimal(0.0)
        var pay = BigDecimal(0.0)
        var fee = BigDecimal(0.0)

        val feeSaved = SavedOverviewResponse()
        val cashSaved = SavedOverviewResponse()

        reportByThisMonth.forEach {
            receive += it.receiveAmount
            pay += it.payAmount
            fee += it.totalFeeBefore
            feeSaved.savedInMonth += it.totalFeeBefore - it.totalFeeAfter
            cashSaved.savedInMonth += it.totalCashBefore - it.totalCashAfter
        }

        previousMonths.forEach {
            val month = appFormatter.formatMonth(it)
            val reports = nettingReportRepository.findAllByMonthAndUser(month, userId)
            var feeSavedAmount = BigDecimal(0.0)
            var cashSavedAmount = BigDecimal(0.0)
            reports.forEach { report ->
                feeSavedAmount += report.totalFeeBefore - report.totalFeeAfter
                cashSavedAmount += report.totalCashBefore - report.totalCashAfter
            }
            feeSaved.savedList.add(
                SavedByMonthResponse(
                    month = month,
                    amount = feeSavedAmount
                )
            )
            cashSaved.savedList.add(
                SavedByMonthResponse(
                    month = month,
                    amount = cashSavedAmount
                )
            )
        }

        feeSaved.savedYTD = feeSaved.savedList.sumOf { it.amount }
        cashSaved.savedYTD = cashSaved.savedList.sumOf { it.amount }

        return NettingOverviewResponse(
            month = thisMonthStr,
            currency = currency,
            receivable = receive,
            payable = pay,
            cashFlow = (pay - receive).abs(),
            feeSaved = feeSaved,
            cashFlowSaved = cashSaved
        )
    }
}