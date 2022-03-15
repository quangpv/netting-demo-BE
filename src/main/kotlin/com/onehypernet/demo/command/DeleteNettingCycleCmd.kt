package com.onehypernet.demo.command

import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.datasource.NettingCycleDao
import org.springframework.stereotype.Service

@Service
class DeleteNettingCycleCmd(
    private val nettingCycleDao: NettingCycleDao,
    private val validator: Validator
) {
    operator fun invoke(id: String) {
        validator.checkNettingId(id)
        nettingCycleDao.deleteById(id)
    }
}