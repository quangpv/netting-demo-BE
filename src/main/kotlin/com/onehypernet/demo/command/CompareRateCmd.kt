package com.onehypernet.demo.command

import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.datasource.WiseApi
import com.onehypernet.demo.extension.call
import com.onehypernet.demo.extension.safe
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.dto.ProviderDTO
import com.onehypernet.demo.model.request.RateCompareRequest
import com.onehypernet.demo.model.response.CompareItemResponse
import com.onehypernet.demo.model.response.RateCompareResponse
import com.onehypernet.demo.repository.MarginRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

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
    private val appFormatter: AppFormatter,
    private val wiseApi: WiseApi,
    private val marginRepository: MarginRepository
) {
    companion object {
        const val INTERBANK_ID = 39L
    }

    operator fun invoke(request: RateCompareRequest): RateCompareResponse {
        validator.checkCurrency(request.homeCurrency)
        validator.checkCurrency(request.invoiceCurrency)
        if (request.homeCurrency == request.invoiceCurrency) return RateCompareResponse(
            BigDecimal(1.0),
            "Just now",
            request.invoiceCurrency,
            request.homeCurrency,
            emptyList()
        )

        val wise = wiseApi.getRate(request.homeCurrency, request.invoiceCurrency, request.amount).call()
            ?: notFoundComparison(request)
        val interbankRate = wise.providers
            ?.find { it.id == INTERBANK_ID }
            ?.quotes?.firstOrNull()?.rate?.let { BigDecimal(it) }
            ?: notFoundComparison(request)

        val margin = marginRepository
            .findByIdOrNull("${request.homeCurrency.trim()}${request.invoiceCurrency.trim()}")
            ?.percent
            ?: throws(
                "Not found margin ${request.homeCurrency} (Home) ${request.invoiceCurrency} (Invoice)," +
                        " Please request admin to upload margins"
            )

        val requestAmount = request.amount / (interbankRate * (BigDecimal.valueOf(1) - margin))
        if (requestAmount.compareTo(BigDecimal.valueOf(0.0)) == 0)
            throws("Send amount of ${request.amount} = 0")

        val wise1 = wiseApi.getRate(request.homeCurrency, request.invoiceCurrency, requestAmount).call()
            ?: notFoundComparison(request, requestAmount)

        val ohnProvider = wise1.providers?.find { it.id == INTERBANK_ID }
            ?: notFoundComparison(request, requestAmount)

        val providers = wise1.providers.filter { it.partner != true }
        val ohnComparison = createComparison(ohnProvider, request)
            ?: notFoundComparison(request, requestAmount)

        val lastTime = appFormatter.formatTimeAgo(ohnProvider.quotes?.firstOrNull()?.dateCollected)

        return RateCompareResponse(
            exchangeRate = ohnComparison.exchangeRate,
            lastTime = lastTime,
            invoiceCurrency = request.invoiceCurrency,
            homeCurrency = request.homeCurrency,
            compares = providers.mapNotNull {
                createComparison(it, request, ohnComparison)
            } + ohnComparison
        )
    }

    private fun notFoundComparison(request: RateCompareRequest, requestAmount: BigDecimal): Nothing {
        throws("Not found comparison ${request.homeCurrency} to ${request.invoiceCurrency} with amount = ${request.amount} (Send amount = $requestAmount)")
    }

    private fun notFoundComparison(request: RateCompareRequest): Nothing =
        throws("Not found comparison ${request.homeCurrency} to ${request.invoiceCurrency} with amount = ${request.amount}")

    private fun createComparison(
        provider: ProviderDTO,
        request: RateCompareRequest,
        ohn: CompareItemResponse? = null
    ): CompareItemResponse? {
        val quotes = provider.quotes?.firstOrNull() ?: return null
        val rate = quotes.rate ?: return null
        val fee = BigDecimal(quotes.fee ?: 0.0)
        val totalPayment = request.amount / rate.toBigDecimal() + fee

        return CompareItemResponse(
            logo = provider.logo.orEmpty(),
            name = provider.name.orEmpty(),
            exchangeRate = BigDecimal.valueOf(rate),
            transferFee = appFormatter.formatAmount(fee),
            totalPayment = appFormatter.formatAmount(totalPayment),
            loss = appFormatter.formatAmount(ohn?.let { totalPayment - it.totalPayment }.safe())
        )
    }

}