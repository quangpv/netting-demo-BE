package com.onehypernet.demo.helper

import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.NettingParamEntity
import java.math.BigDecimal
import kotlin.random.Random

interface AmountConverter {
    fun getLocal(amount: BigDecimal, currency: String): BigDecimal
    fun getFee(amount: BigDecimal, currency: String): BigDecimal
    fun generateTotalFeeAfter(totalFeeBefore: BigDecimal): BigDecimal
}

class AmountConverterImpl(
    private val localCurrency: String,
    private val params: List<NettingParamEntity>,
) : AmountConverter {
    private val mLookup = hashMapOf<String, NettingParamEntity>()

    init {
        params.forEach {
            mLookup[keyOf(it.fromCurrency, it.toCurrency)] = it
        }
    }

    override fun getLocal(amount: BigDecimal, currency: String): BigDecimal {
        return amount * rateOf(currency, localCurrency).toBigDecimal()
    }

    override fun getFee(amount: BigDecimal, currency: String): BigDecimal {
        val localAmount = getLocal(amount, currency).toDouble()
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
            ?: error("Not found netting params from $fromCurrency to $toCurrency")
        return 1 / rate.exchangeRate
    }

    private fun keyOf(fromCurrency: String, toCurrency: String): String {
        return "${fromCurrency}#${toCurrency}"
    }

    override fun generateTotalFeeAfter(totalFeeBefore: BigDecimal): BigDecimal {
        return totalFeeBefore * BigDecimal.valueOf(Random.nextDouble(5.0, 95.0) / 100)
    }
}