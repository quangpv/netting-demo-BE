package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.NettedTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NettedTransactionRepository : JpaRepository<NettedTransactionEntity, String> {
    fun findAllByNettingId(nettingId: String): List<NettedTransactionEntity>

    @Query(
        "select * from NETTED_TRANSACTION " +
                "where FORMATDATETIME(DATE,'yyyy-MM') = ?1 " +
                "and USER_ID=?2", nativeQuery = true
    )
    fun findAllByMonthAndUser(month: String, userId: String): List<NettedTransactionEntity>
}
