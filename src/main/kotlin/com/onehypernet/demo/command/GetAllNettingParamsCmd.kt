package com.onehypernet.demo.command

import com.onehypernet.demo.component.CSVReader
import com.onehypernet.demo.model.request.NettingParamRequest
import com.onehypernet.demo.model.response.ExchangeRateResponse
import com.onehypernet.demo.model.response.ListResponse
import com.onehypernet.demo.model.response.MetadataResponse
import com.onehypernet.demo.repository.NettingParamRepository
import org.springframework.stereotype.Service

@Service
class GetAllNettingParamsCmd(
    private val nettingParamRepository: NettingParamRepository,
    private val csvReader: CSVReader
) {
    operator fun invoke(request: NettingParamRequest): ListResponse<ExchangeRateResponse> {
        val filterFroms = csvReader.readLine(request.from).map { it.toUpperCase().trim() }
        val filterTos = csvReader.readLine(request.to).map { it.toUpperCase().trim() }
        val filterLocations = csvReader.readLine(request.location).map { it.toUpperCase().trim() }
        val filterDestinations = csvReader.readLine(request.destinations).map { it.toUpperCase().trim() }

        val result = nettingParamRepository.findAllByToday()

        val shouldFilter = filterFroms.isNotEmpty()
                || filterTos.isNotEmpty()
                || filterLocations.isNotEmpty()
                || filterDestinations.isNotEmpty()

        val list = result.asSequence()
            .filter {
                if (!shouldFilter) return@filter true
                var shouldAccept = false
                shouldAccept = tryToAccept(it.fromCurrency, filterFroms, shouldAccept)
                shouldAccept = tryToAccept(it.toCurrency, filterTos, shouldAccept)
                shouldAccept = tryToAccept(it.atLocationCode, filterLocations, shouldAccept)

                if (filterDestinations.isNotEmpty()) {
                    shouldAccept = shouldAccept || hasIntersect(it.destinationLocations, filterDestinations)
                }
                shouldAccept
            }
            .map {
                ExchangeRateResponse(
                    id = it.id,
                    fromCurrency = it.fromCurrency,
                    toCurrency = it.toCurrency,
                    margin = it.margin,
                    fee = it.fee,
                    minFee = it.minFee,
                    maxFee = it.maxFee,
                    fixedFee = it.fixedFee,
                    exchangeRate = it.exchangeRate,
                    atLocation = it.atLocationCode,
                    destinationLocations = it.destinationLocations
                )
            }.toList()
        return ListResponse(list, MetadataResponse(1, list.size))
    }

    private fun hasIntersect(destinationLocations: String, filterDestinations: List<String>): Boolean {
        return csvReader.readLine(destinationLocations).map { it.toUpperCase().trim() }
            .intersect(filterDestinations).isNotEmpty()
    }

    private fun tryToAccept(value: String, values: List<String>, shouldAccept: Boolean): Boolean {
        if (values.isNotEmpty()) {
            return value in values || shouldAccept
        }
        return shouldAccept
    }
}
