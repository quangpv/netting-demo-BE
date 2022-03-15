package com.onehypernet.demo.repository

import com.onehypernet.demo.component.FileStorage
import com.onehypernet.demo.datasource.UploadedTransactionFileDao
import com.onehypernet.demo.model.entity.UploadedTransactionFileEntity
import com.onehypernet.demo.model.entity.UploadedTransactionId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class TransactionFileRepository(
    private val uploadedTransactionFileDao: UploadedTransactionFileDao,
    private val fileStorage: FileStorage
) {

    fun save(userId: String, nettingId: String, file: MultipartFile) {
        val fileExt = file.originalFilename?.split(".")?.lastOrNull() ?: ""
        val uploaded = UploadedTransactionFileEntity(
            nettingId = nettingId,
            userId = userId,
            fileName = file.originalFilename ?: "Unknown_${System.currentTimeMillis()}",
            storedFileName = if (fileExt.isNotBlank()) "${userId}.${fileExt}" else userId
        )
        uploadedTransactionFileDao.save(uploaded)
        fileStorage.save(uploaded.nettingId, uploaded.storedFileName, file)
    }

    fun findByUserAndNetting(userId: String, nettingId: String): UploadedTransactionFileEntity? {
        return uploadedTransactionFileDao.findByIdOrNull(UploadedTransactionId(nettingId, userId))
    }
}