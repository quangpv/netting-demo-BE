package com.onehypernet.demo.repository

import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.datasource.NettingParamDao
import com.onehypernet.demo.model.entity.NettingParamEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Component
class NettingParamRepository(
    private val nettingParamDao: NettingParamDao,
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val appFormatter: AppFormatter
) {
    fun deleteAllCreatedByToDay() {
        transactionTemplate.execute {
            val query = entityManager.createNativeQuery(
                "DELETE FROM NETTING_PARAM " +
                        "WHERE FORMATDATETIME(CREATE_AT,'yyyy-MM-dd') = FORMATDATETIME(CURDATE(),'yyyy-MM-dd')"
            )
            query.executeUpdate()
            it.flush()
        }
    }

    fun saveAll(result: List<NettingParamEntity>) {
        nettingParamDao.saveAll(result)
    }

    fun findAllByToday(): List<NettingParamEntity> {
        return nettingParamDao.findAllByCurrentDay()
    }

    fun findAllByDate(createAt: LocalDateTime): List<NettingParamEntity> {
        return nettingParamDao.findAllByDate(appFormatter.formatRequestDate(createAt))
    }

    fun removeAll() {
        nettingParamDao.deleteAll()
    }

    fun findAll(): List<NettingParamEntity> {
        return nettingParamDao.findAll()
    }
}