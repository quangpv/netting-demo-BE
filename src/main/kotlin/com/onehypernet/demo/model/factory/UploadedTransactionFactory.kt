package com.onehypernet.demo.model.factory

import com.onehypernet.demo.component.AppCalendar
import com.onehypernet.demo.component.CSVReader
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.model.enumerate.TransactionType
import com.onehypernet.demo.model.vo.TransactionVO
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class UploadedTransactionFactory(
    private val csvReader: CSVReader,
    private val appCalendar: AppCalendar,
    private val validator: Validator,
) {
    fun createList(inputStream: InputStream): List<TransactionVO> {
        return csvReader.readStream(inputStream) { line ->
            val firstCell = line[0].trim()
            if (firstCell.toLowerCase() == "date") return@readStream null
            val date = firstCell.let { appCalendar.dateOf(it) }
            val dueDate = line[1].trim().let { appCalendar.dateOf(it) }
            val id = line[2].trim().also { validator.checkId(it) }
            val type = line[3].trim().let { TransactionType[it] }
            val counterParty = line[4].trim().also { validator.checkCounterPartyName(it) }
            val currency = line[5].trim().toUpperCase().also { validator.checkCurrency(it) }
            val amount = line[6].trim().toBigDecimal()

            TransactionVO(
                id = id,
                date = date,
                dueDate = dueDate,
                type = type,
                counterPartyName = counterParty,
                currency = currency,
                amount = amount
            )
        }

    }
}