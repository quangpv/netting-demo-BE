package com.onehypernet.demo.helper

import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.NettingParamEntity
import java.math.BigDecimal

interface ReportCalculator {
    fun getLocalAmount(amount: BigDecimal, currency: String): BigDecimal
    fun getFeeAmount(amount: BigDecimal, currency: String): BigDecimal
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
}