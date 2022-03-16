package com.onehypernet.demo.helper

import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.NettedTransactionEntity
import com.onehypernet.demo.model.entity.NettingParamEntity
import com.onehypernet.demo.model.entity.ReportParamEntity
import java.math.BigDecimal
import kotlin.math.log

interface ReportCalculator {
    fun getLocalAmount(amount: BigDecimal, currency: String): BigDecimal
    fun getFeeAmount(amount: BigDecimal, currency: String): BigDecimal
    fun getLocalAmount(entity: NettedTransactionEntity): BigDecimal {
        return getLocalAmount(entity.amount, entity.currency)
    }

    fun getFeeAmount(entity: NettedTransactionEntity): BigDecimal {
        return getFeeAmount(entity.amount, entity.currency)
    }

    fun getFeeAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal
    fun getSavingAmount(before: BigDecimal, after: BigDecimal): BigDecimal
    fun getCashAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal
    fun getPotential(savingCash: BigDecimal, savingFee: BigDecimal, transactionCount: Int): Double
}

class ReportCalculatorImpl(
    private val localCurrency: String,
    private val params: List<NettingParamEntity>,
) : ReportCalculator {
    private val mLookup = hashMapOf<String, NettingParamEntity>()

    init {
        params.forEach {
            mLookup[keyOf(it.fromCurrency, it.toCurrency)] = it
        }
    }

    override fun getLocalAmount(amount: BigDecimal, currency: String): BigDecimal {
        return amount * rateOf(currency, localCurrency).toBigDecimal()
    }

    override fun getFeeAmount(amount: BigDecimal, currency: String): BigDecimal {
        val localAmount = getLocalAmount(amount, currency).toDouble()
        val param = nettingParamOf(localCurrency, currency)
        val vFee = minOf(maxOf(localAmount * param.fee / 100, param.minFee), param.maxFee)
        return BigDecimal.valueOf(localAmount * param.margin / 100 + vFee + param.fixedFee)
    }

    override fun getFeeAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal {
        return before * BigDecimal(param.savingFeePercent / 100)
    }

    override fun getSavingAmount(before: BigDecimal, after: BigDecimal): BigDecimal {
        return before - after
    }

    override fun getCashAfterAmount(before: BigDecimal, param: ReportParamEntity): BigDecimal {
        return before * BigDecimal(param.savingCashPercent / 100)
    }

    private fun nettingParamOf(fromCurrency: String, toCurrency: String): NettingParamEntity {
        if (fromCurrency == toCurrency) return NettingParamEntity()
        return mLookup[keyOf(fromCurrency, toCurrency)]
            ?: mLookup[keyOf(toCurrency, fromCurrency)]
            ?: throws("Not found netting param from $fromCurrency to $toCurrency")
    }

    private fun rateOf(fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return 1.0

        var rate = mLookup[keyOf(fromCurrency, toCurrency)]
        if (rate != null) return rate.exchangeRate
        rate = mLookup[keyOf(toCurrency, fromCurrency)]
            ?: error("Not found exchange rate from $fromCurrency to $toCurrency")
        return 1 / rate.exchangeRate
    }

    private fun keyOf(fromCurrency: String, toCurrency: String): String {
        return "${fromCurrency}#${toCurrency}"
    }

    override fun getPotential(savingCash: BigDecimal, savingFee: BigDecimal, transactionCount: Int): Double {
        return 0.35 * (savingCash + savingFee).toDouble() + (0.3 * minOf(
            log(transactionCount.toDouble(), 300.0), 0.95
        ))
    }
}