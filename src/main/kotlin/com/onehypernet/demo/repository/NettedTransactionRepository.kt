package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.NettedTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NettedTransactionRepository : JpaRepository<NettedTransactionEntity, String> {
    fun findAllByNettingIdAndUserId(nettingId: String, userId: String): List<NettedTransactionEntity>
}
