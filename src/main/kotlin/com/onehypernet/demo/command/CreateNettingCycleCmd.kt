package com.onehypernet.demo.command

import com.onehypernet.demo.component.NettingIdGenerator
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.model.entity.NettingCycleEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.factory.NettingCycleFactory
import com.onehypernet.demo.model.request.NettingCycleRequest
import com.onehypernet.demo.model.response.NettingCycleResponse
import org.springframework.stereotype.Service

@Service
class CreateNettingCycleCmd(
    private val nettingIdGenerator: NettingIdGenerator,
    private val validator: Validator,
    private val nettingCycleDao: NettingCycleDao,
    private val nettingCycleFactory: NettingCycleFactory
) {
    operator fun invoke(request: NettingCycleRequest): NettingCycleResponse {
        validator.checkNettingGroup(request.group)
        val id = nettingIdGenerator.generate()
        val newCycle = NettingCycleEntity(
            id = id,
            nettingGroup = request.group.trim(),
            status = NettingStatus.Open,
        )
        val entity = nettingCycleDao.save(newCycle)
        return nettingCycleFactory.create(entity)
    }
}