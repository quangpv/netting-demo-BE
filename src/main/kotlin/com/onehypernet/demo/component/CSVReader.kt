package com.onehypernet.demo.component

import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class CSVReader {
    private val settings = CsvParserSettings()

    fun <T> readStream(inputStream: InputStream, map: (Array<String>) -> T?): List<T> {
        val parser = CsvParser(settings)
        return parser.parseAll(inputStream).mapNotNull { map(it) }
    }

    fun readLine(line: String): Array<String> {
        if (line.isBlank()) return emptyArray()
        return CsvParser(settings).parseLine(line)
    }
}