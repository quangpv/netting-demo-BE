package com.onehypernet.demo.command

import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.component.NettingIdGenerator
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.model.entity.NettingCycleEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.request.NettingCycleRequest
import com.onehypernet.demo.model.response.NettingCycleResponse
import org.springframework.stereotype.Service

@Service
class CreateNettingCycleCmd(
    private val nettingIdGenerator: NettingIdGenerator,
    private val validator: Validator,
    private val nettingCycleDao: NettingCycleDao,
    private val appFormatter: AppFormatter
) {
    operator fun invoke(request: NettingCycleRequest): NettingCycleResponse {
        validator.checkNettingGroup(request.group)
        val lastId = nettingCycleDao.findLast()?.id
        val id = nettingIdGenerator.generate(lastId)

        val newCycle = NettingCycleEntity(
            id = id,
            nettingGroup = request.group.trim(),
            status = NettingStatus.Open,
        )
        val entity = nettingCycleDao.save(newCycle)
        return NettingCycleResponse(
            id = entity.id,
            group = entity.nettingGroup,
            status = entity.status,
            createAt = appFormatter.formatDate(entity.createAt),
        )
    }
}