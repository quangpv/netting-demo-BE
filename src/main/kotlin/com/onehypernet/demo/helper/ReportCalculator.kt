package com.onehypernet.demo.helper

import com.onehypernet.demo.extension.divideTo
import com.onehypernet.demo.model.entity.ReportParamEntity
import java.math.BigDecimal
import kotlin.math.log

interface ReportCalculator {
    fun getFeeAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal
    fun getSavingAmount(before: BigDecimal, after: BigDecimal): BigDecimal
    fun getCashAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal
    fun getPotential(savingCash: BigDecimal, savingFee: BigDecimal, transactionCount: Int): Double
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

    override fun getPotential(savingCash: BigDecimal, savingFee: BigDecimal, transactionCount: Int): Double {
        val potential = 0.35 * (savingCash + savingFee).toDouble() + (0.3 * log(transactionCount.toDouble(), 300.0))
        return minOf(potential, 0.95)
    }

    override fun calculateSavingPercent(totalBefore: BigDecimal, totalAfter: BigDecimal): Double {
        return totalAfter.divideTo(totalBefore).toDouble() * 100
    }
}