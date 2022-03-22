package com.onehypernet.demo.command

import com.onehypernet.demo.component.AppCalendar
import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.datasource.WiseApi
import com.onehypernet.demo.extension.call
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.dto.ProviderDTO
import com.onehypernet.demo.model.request.RateCompareRequest
import com.onehypernet.demo.model.response.CompareItemResponse
import com.onehypernet.demo.model.response.RateCompareResponse
import com.onehypernet.demo.repository.ForexRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Compare rates
Available currencies
InvoiceCurrency (targetCurrency in the API)= SGD, AUD, CAD, CHF, CNY, EUR, GBP, HKD, IDR, INR, JPY, KRW, MYR, NZD, PHP, RUB, THB, TRY, USD, VND
HomeCurrency (sourceCurrency in the API) = SGD,AUD,CAD,CHF,EUR,GBP,HKD,IDR,INR,JPY,MYR,NZD,USD
Parameters
InvoiceAmount (20500)
InvoiceCurrency (SGD)
HomeCurrency (EUR)
1) Call polygon interbank API to retrieve interbank rate (e.g. EUR/SGD rate = 1.52709)
2) Look up Wise margin for currency pair (uploaded by admin) to calculate sendAmount based on InvoiceAmount (20500)
e.g. Invoice currency: SGD (targetCurrency = SGD), Home currency: EUR (sourceCurrency = EUR)
look up EUR/SGD margin = 0.53%. sendAmount = InvoiceAmount/(interbank rate*(1-margin)) = 20500 / (1.52709*(1-0.53%)) = 13495.6
3) Use api.wise to retrieve [rate] and [fee] for sendAmount = 13495.6
- ignore providers where partner: true
Exchange Rate in UI = [rate], Transfer fee in UI = [fee], Total payment in UI
- to calculate Total Payment = InvoiceAmount / rate + fee = 20500/(e.g. 1.517) + e.g. 31 = 13544.50
- no need to calculate for Wise (id = 39) because Wise total payment is = sendAmount
- OneHypernet total payment = InvoiceAmount / rate = 20500 / 1.52709 = 13424.23
- to calculate Loss = Total Payment for provider - Total Payment by OneHypernet
 */
@Service
class CompareRateCmd(
    private val validator: Validator,
    private val forexRepository: ForexRepository,
    private val appFormatter: AppFormatter,
    private val wiseApi: WiseApi,
    private val appCalendar: AppCalendar
) {
    operator fun invoke(request: RateCompareRequest): RateCompareResponse {
        validator.checkCurrency(request.homeCurrency)
        validator.checkCurrency(request.invoiceCurrency)

        val wise = wiseApi.getRate(request.homeCurrency, request.invoiceCurrency, request.amount).call()
            ?: notFoundComparison(request)
        val provider = wise.providers?.find { it.id == 39L }
            ?: notFoundComparison(request)

        val ohnComparison = createOHNComparison(provider, request)
        val lastTime = appFormatter.formatTimeAgo(provider.quotes?.firstOrNull()?.dateCollected)
        return RateCompareResponse(
            exchangeRate = ohnComparison.exchangeRate,
            lastTime = lastTime,
            invoiceCurrency = request.invoiceCurrency,
            homeCurrency = request.homeCurrency,
            compares = listOf(
                ohnComparison
            )
        )
    }

    private fun notFoundComparison(request: RateCompareRequest): Nothing =
        throws("Not found comparison ${request.homeCurrency} to ${request.invoiceCurrency}")

    private fun createOHNComparison(provider: ProviderDTO, request: RateCompareRequest): CompareItemResponse {
        val rate = provider.quotes?.firstOrNull()?.rate ?: notFoundComparison(request)

        return CompareItemResponse(
            logo = provider.logo.orEmpty(),
            exchangeRate = rate,
            transferFee = BigDecimal(0.0),
            totalPayment = appFormatter.formatAmount(request.amount * BigDecimal(rate)),
            loss = BigDecimal(0.0)
        )
    }
}