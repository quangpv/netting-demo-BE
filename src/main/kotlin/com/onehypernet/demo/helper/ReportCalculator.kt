package com.onehypernet.demo.helper

import com.onehypernet.demo.extension.divideTo
import com.onehypernet.demo.model.entity.ReportParamEntity
import java.math.BigDecimal
import kotlin.math.log

interface ReportCalculator {
    fun getFeeAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal
    fun getSavingAmount(before: BigDecimal, after: BigDecimal): BigDecimal
    fun getCashAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal

    /**
     * 35% * fee_savings + 35% * cashflow_savings + 30% * min of (log(transaction count uploaded by user) or 95%)
     */
    fun getPotentialPercent(savingCashPercent: Double, savingFeePercent: Double, transactionCount: Int): Double
    fun calculateSavingPercent(totalBefore: BigDecimal, totalAfter: BigDecimal): Double
}

class ReportCalculatorImpl : ReportCalculator {

    override fun getFeeAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal {
        return before * BigDecimal(param.savingFeePercent / 100)
    }

    override fun getSavingAmount(before: BigDecimal, after: BigDecimal): BigDecimal {
        return before - after
    }

    override fun getCashAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal {
        return before * BigDecimal(param.savingCashPercent / 100)
    }

    override fun getPotentialPercent(
        savingCashPercent: Double,
        savingFeePercent: Double,
        transactionCount: Int
    ): Double {
        val rate = 0.35 * (savingCashPercent + savingFeePercent) / 100 + (0.3 * minOf(
            log(transactionCount.toDouble(), 300.0),
            95.0
        ))
        return rate * 100
    }

    override fun calculateSavingPercent(totalBefore: BigDecimal, totalAfter: BigDecimal): Double {
        return totalAfter.divideTo(totalBefore).toDouble() * 100
    }
}