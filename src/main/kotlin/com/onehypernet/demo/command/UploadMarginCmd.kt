package com.onehypernet.demo.command

import com.onehypernet.demo.component.CSVReader
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.MarginEntity
import com.onehypernet.demo.repository.MarginRepository
import org.springframework.stereotype.Service
import java.io.InputStream
import java.math.BigDecimal
import javax.transaction.Transactional

@Service
open class UploadMarginCmd(
    private val marginRepository: MarginRepository,
    private val csvReader: CSVReader,
) {
    @Transactional
    open operator fun invoke(stream: InputStream) {
        val result = csvReader.readStream(stream) {
            val pair = it[0].split("/")
            val percent = it[1]
                .trim()
                .removeSuffix("%")
                .toDoubleOrNull()?.let { s -> BigDecimal(s) }
                ?: BigDecimal(0.0)
            if (pair.size != 2) throws("Pair ${it[0]} is incorrect")

            MarginEntity(
                id = pair.joinToString("") { s -> s.trim() },
                percent = percent
            )
        }
        marginRepository.deleteAll()
        marginRepository.saveAll(result)
    }
}