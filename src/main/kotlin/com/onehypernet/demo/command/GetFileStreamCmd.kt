package com.onehypernet.demo.command

import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.repository.TransactionFileRepository
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.FileInputStream

@Service
class GetFileStreamCmd(private val fileRepository: TransactionFileRepository) {
    operator fun invoke(userId: String, fileName: String): ResponseEntity<InputStreamResource> {
        val file = fileRepository.getFile(userId, fileName)
        if (!file.exists()) throws("File $fileName is not exists")
        val resource = InputStreamResource(FileInputStream(file))
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(file.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)
    }
}