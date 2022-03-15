package com.onehypernet.demo.command

import com.onehypernet.demo.component.CSVReader
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.model.entity.NettingParamEntity
import com.onehypernet.demo.repository.ForexRepository
import com.onehypernet.demo.repository.NettingParamRepository
import org.springframework.stereotype.Service
import java.io.InputStream
import javax.transaction.Transactional


@Service
open class UploadNettingParamsCmd(
    private val csvReader: CSVReader,
    private val validator: Validator,
    private val nettingParamRepository: NettingParamRepository,
    private val forexRepository: ForexRepository
) {

    @Transactional
    open operator fun invoke(paramsStream: InputStream) {
        forexRepository.tryFetchAll()
        val params = csvReader.readStream(paramsStream) {
            val fromCurrency = it[0].trim()
            val toCurrency = it[1].trim()
            val margin = it[2].toDoubleOrNull() ?: 0.0
            val fee = it[3].toDoubleOrNull() ?: 0.0
            val minFee = it[4].toDoubleOrNull() ?: 0.0
            val maxFee = it[5].toDoubleOrNull() ?: 0.0
            val fixedFee = it[6].toDoubleOrNull() ?: 0.0
            val atLocation = it[7].trim()
            val destinationLocations = it[8].trim()

            if (fromCurrency.toLowerCase() == "from") return@readStream null

            validator.checkCurrency(fromCurrency)
            validator.checkCurrency(toCurrency)
            validator.requirePositive(margin) { it.joinToString() }
            validator.requirePositive(fee) { it.joinToString() }
            validator.requirePositive(minFee) { it.joinToString() }
            validator.requirePositive(maxFee) { it.joinToString() }
            validator.requirePositive(fixedFee) { it.joinToString() }

            validator.checkLocation(atLocation)
            csvReader.readLine(destinationLocations).forEach(validator::checkLocation)

            NettingParamEntity(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                margin = margin,
                fee = fee,
                minFee = minFee,
                maxFee = maxFee,
                fixedFee = fixedFee,
                atLocationCode = atLocation,
                destinationLocations = destinationLocations,
                exchangeRate = forexRepository.requireBy(fromCurrency, toCurrency).exchangeRate
            )
        }

        nettingParamRepository.deleteAllCreatedByToDay()
        nettingParamRepository.saveAll(params)
    }
}