package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.UploadedTransactionFileEntity
import com.onehypernet.demo.model.entity.UploadedTransactionId
import org.springframework.data.jpa.repository.JpaRepository

interface UploadedTransactionFileDao : JpaRepository<UploadedTransactionFileEntity, UploadedTransactionId> {

}