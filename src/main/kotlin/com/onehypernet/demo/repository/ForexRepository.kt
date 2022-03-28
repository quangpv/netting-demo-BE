package com.onehypernet.demo.repository

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppCalendar
import com.onehypernet.demo.datasource.ForexApi
import com.onehypernet.demo.datasource.ForexDao
import com.onehypernet.demo.datasource.LastFetchCache
import com.onehypernet.demo.extension.call
import com.onehypernet.demo.model.entity.ForexEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ForexRepository(
    private val forexDao: ForexDao,
    private val lastFetchCache: LastFetchCache,
    private val forexApi: ForexApi,
    private val appCalendar: AppCalendar,
) {

    private fun shouldFetch(): Boolean {
        return System.currentTimeMillis() - lastFetchCache[AppConst.FOREX] > 5 * 60 * 1000 || forexDao.count() == 0L
    }

    private fun fetchAll(date: String) {
        val result = forexApi.getGroup(date).call()?.results.orEmpty()
        val entities = result.map {
            val symbols = it.exchangeSymbols.orEmpty().removePrefix("C:")
            ForexEntity(symbols, it.openRate ?: 0.0)
        }
        forexDao.saveAll(entities)
        lastFetchCache.markAsLastFetch(AppConst.FOREX)
    }

    fun requireBy(_fromCurrency: String, _toCurrency: String): ForexEntity {
        val fromCurrency = _fromCurrency.toUpperCase()
        val toCurrency = _toCurrency.toUpperCase()

        if (fromCurrency == toCurrency) return ForexEntity(
            "${fromCurrency}${toCurrency}",
            1.0
        )
        var exchangeRate = findBy(fromCurrency, toCurrency)
        if (exchangeRate == null) {
            exchangeRate = ForexEntity(
                "${fromCurrency}${toCurrency}",
                getExchangeRate(fromCurrency, toCurrency)
            )
        }
        return exchangeRate
    }

    private fun findBy(fromCurrency: String, toCurrency: String): ForexEntity? {
        return forexDao.findByIdOrNull(keyOf(fromCurrency, toCurrency))
    }

    private fun keyOf(fromCurrency: String, toCurrency: String): String {
        return "$fromCurrency$toCurrency"
    }

    private fun getExchangeRate(fromCurrency: String, toCurrency: String): Double {
        val f0 = findBy(toCurrency, fromCurrency)
        if (f0 != null) {
            return 1 / f0.exchangeRate
        }

        val f1 = findBy(fromCurrency, AppConst.BRIDGING_CURRENCY)
        val t1 = findBy(AppConst.BRIDGING_CURRENCY, toCurrency)
        if (f1 != null && t1 != null) {
            return f1.exchangeRate * t1.exchangeRate
        }

        val f2 = findBy(AppConst.BRIDGING_CURRENCY, fromCurrency)
        val t2 = findBy(AppConst.BRIDGING_CURRENCY, toCurrency)

        if (f2 != null && t2 != null) {
            return (1 / f2.exchangeRate) * t2.exchangeRate
        }

        val f3 = findBy(fromCurrency, AppConst.BRIDGING_CURRENCY)
        val t3 = findBy(toCurrency, AppConst.BRIDGING_CURRENCY)

        if (f3 != null && t3 != null) {
            return f3.exchangeRate / t3.exchangeRate
        }

        val f4 = findBy(AppConst.BRIDGING_CURRENCY, fromCurrency)
        val t4 = findBy(toCurrency, AppConst.BRIDGING_CURRENCY)

        if (f4 != null && t4 != null) {
            return 1 / (f4.exchangeRate * t4.exchangeRate)
        }
        throw IllegalArgumentException("Not found exchange rate $fromCurrency to $toCurrency")
    }

    fun tryFetchAll() {
        fetchAll(appCalendar.nowStr())
    }
}
