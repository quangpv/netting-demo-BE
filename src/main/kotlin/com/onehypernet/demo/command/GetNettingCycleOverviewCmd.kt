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
    companion object {
        val FAKE_FEE = hashMapOf(
            9 to 851.36,
            10 to 1185.94,
            11 to 1658.15,
            12 to 2563.82,
            1 to 3254.21,
            2 to 3687.25,
            3 to 4123.25,
            4 to 4536.78,
            5 to 4825.36,
        )
        val FAKE_CASH = hashMapOf(
            9 to 25485.91,
            10 to 36258.78,
            11 to 41278.14,
            12 to 48982.56,
            1 to 57841.87,
            2 to 69785.45,
            3 to 79639.65,
            4 to 89525.78,
            5 to 115785.21,
        )
    }

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
        }
        cashSaved.savedInMonth = receive

        previousMonths.forEach {
            val month = appFormatter.formatMonth(it)
            val monthValue = it.monthValue
            val reports = nettingReportRepository.findAllByMonthAndUser(month, userId)
            var feeSavedAmount = BigDecimal(0.0)
            var cashSavedAmount = BigDecimal(0.0)
            reports.forEach { report ->
                feeSavedAmount += report.totalFeeBefore - report.totalFeeAfter
                cashSavedAmount += report.receiveAmount
            }

            if (feeSavedAmount == BigDecimal.ZERO && cashSavedAmount == BigDecimal.ZERO) {
                cashSavedAmount = FAKE_CASH[monthValue]?.toBigDecimal() ?: BigDecimal.ZERO
                feeSavedAmount = FAKE_FEE[monthValue]?.toBigDecimal() ?: BigDecimal.ZERO
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