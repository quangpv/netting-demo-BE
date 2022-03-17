package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.NettingReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NettingReportRepository : JpaRepository<NettingReportEntity, String> {
    @Query(
        "select * from NETTING_REPORT where FORMATDATETIME(CREATE_AT,'yyyy-MM') = ?1 " +
                "and USER_ID = ?2", nativeQuery = true
    )
    fun findAllByMonthAndUser(month: String, userId: String): List<NettingReportEntity>

}