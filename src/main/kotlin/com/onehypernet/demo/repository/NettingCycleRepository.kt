package com.onehypernet.demo.repository

import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.NettingCycleEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class NettingCycleRepository(private val nettingCycleDao: NettingCycleDao) {
    fun requireById(nettingId: String): NettingCycleEntity {
        return nettingCycleDao.findByIdOrNull(nettingId)
            ?: throws("Not found Netting cycle $nettingId")
    }

    fun save(entity: NettingCycleEntity) {
        nettingCycleDao.save(entity)
    }
}