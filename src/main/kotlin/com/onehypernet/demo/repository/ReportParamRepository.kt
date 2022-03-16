package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.ReportParamEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import kotlin.random.Random

interface ReportParamDao : JpaRepository<ReportParamEntity, String> {}

@Component
class ReportParamRepository(private val dao: ReportParamDao) {
    fun findOrCreate(nettingId: String): ReportParamEntity {
        val entity = dao.findByIdOrNull(nettingId)
        if (entity != null) return entity
        return dao.save(
            ReportParamEntity(
                nettingId = nettingId,
                savingFeePercent = Random.nextDouble(5.0, 95.0),
                savingCashPercent = Random.nextDouble(5.0, 95.0)
            )
        )
    }

}