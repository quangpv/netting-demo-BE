package com.onehypernet.demo.component

import com.onehypernet.demo.model.vo.ICSVRecord
import com.opencsv.CSVWriter
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileWriter


interface FileStorage {
    fun save(nettingId: String, fileName: String, file: MultipartFile)
    fun save(nettingId: String, fileName: String, data: List<ICSVRecord>)
}

@Component("FileStorage")
class LocalFileStorage : FileStorage {
    companion object {
        private const val FOLDER = "transactions"
    }

    override fun save(nettingId: String, fileName: String, file: MultipartFile) {
        File(getFolder(), fileName).writeBytes(file.inputStream.readAllBytes())
    }

    private fun getFolder(): File {
        return File(FOLDER).also { it.mkdirs() }
    }

    override fun save(nettingId: String, fileName: String, data: List<ICSVRecord>) {
        val writer = CSVWriter(FileWriter(File(getFolder(), fileName)))
        writer.writeAll(data.map { it.toRow() })
        writer.close()
    }
}