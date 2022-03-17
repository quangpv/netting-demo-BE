package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.NettingParamEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NettingParamDao : JpaRepository<NettingParamEntity, String> {
    @Query(
        "select * from NETTING_PARAM " +
                "WHERE FORMATDATETIME(CREATE_AT,'yyyy-MM-dd') = FORMATDATETIME(CURDATE(),'yyyy-MM-dd') " +
                "and NETTING_ID = ?1",
        nativeQuery = true
    )
    fun findAllByCurrentDayAndNettingId(nettingId: String): List<NettingParamEntity>

    @Query(
        "select * from NETTING_PARAM " +
                "WHERE FORMATDATETIME(CREATE_AT,'yyyy-MM-dd') = FORMATDATETIME(CURDATE(),'yyyy-MM-dd')",
        nativeQuery = true
    )
    fun findAllByCurrentDay(): List<NettingParamEntity>

    @Query(
        "select * from NETTING_PARAM " +
                "WHERE FORMATDATETIME(CREATE_AT,'yyyy-MM-dd') = ?1",
        nativeQuery = true
    )
    fun findAllByDate(date: String): List<NettingParamEntity>
}