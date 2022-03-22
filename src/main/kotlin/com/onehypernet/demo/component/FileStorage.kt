package com.onehypernet.demo.component

import com.onehypernet.demo.model.vo.ICSVRecord
import com.opencsv.CSVWriter
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileWriter


interface FileStorage {
    fun save(userId: String, fileName: String, file: MultipartFile)
    fun save(userId: String, fileName: String, data: List<ICSVRecord>)
    fun getFile(userId: String, fileName: String): File
}

@Component("FileStorage")
class LocalFileStorage : FileStorage {
    companion object {
        private const val FOLDER = "transactions"
    }

    override fun save(userId: String, fileName: String, file: MultipartFile) {
        File(getFolder(userId), fileName).writeBytes(file.inputStream.readAllBytes())
    }

    private fun getFolder(userId: String): File {
        return File(FOLDER, userId).also { it.mkdirs() }
    }

    override fun save(userId: String, fileName: String, data: List<ICSVRecord>) {
        val writer = CSVWriter(FileWriter(File(getFolder(userId), fileName)))
        writer.writeAll(data.map { it.toRow() })
        writer.close()
    }

    override fun getFile(userId: String, fileName: String): File {
        return File(getFolder(userId), fileName)
    }
}