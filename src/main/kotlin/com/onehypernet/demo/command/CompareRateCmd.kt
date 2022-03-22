package com.onehypernet.demo.command

import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.model.request.RateCompareRequest
import com.onehypernet.demo.model.response.CompareItemResponse
import com.onehypernet.demo.model.response.RateCompareResponse
import com.onehypernet.demo.repository.ForexRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CompareRateCmd(
    private val validator: Validator,
    private val forexRepository: ForexRepository,
    private val appFormatter: AppFormatter
) {
    operator fun invoke(request: RateCompareRequest): RateCompareResponse {
        validator.checkCurrency(request.homeCurrency)
        validator.checkCurrency(request.invoiceCurrency)

        val ohnForex = forexRepository.requireBy(request.homeCurrency, request.invoiceCurrency)
        val ohnComparison = CompareItemResponse(
            logo = "",
            exchangeRate = ohnForex.exchangeRate,
            transferFee = BigDecimal(0.0),
            totalPayment = appFormatter.formatAmount(request.amount * BigDecimal(ohnForex.exchangeRate)),
            loss = BigDecimal(0.0)
        )
        val lastTime = appFormatter.formatTimeAgo(ohnForex.createAt)

        return RateCompareResponse(
            exchangeRate = ohnForex.exchangeRate,
            lastTime = lastTime,
            invoiceCurrency = request.invoiceCurrency,
            homeCurrency = request.homeCurrency,
            compares = listOf(
                ohnComparison
            )
        )
    }
}