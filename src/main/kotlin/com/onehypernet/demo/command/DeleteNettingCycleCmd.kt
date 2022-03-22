package com.onehypernet.demo.command

import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.repository.NettingCycleRepository
import com.onehypernet.demo.repository.NettingReportRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class DeleteNettingCycleCmd(
    private val nettingCycleRepository: NettingCycleRepository,
    private val validator: Validator,
    private val nettingReportRepository: NettingReportRepository
) {

    @Transactional
    open operator fun invoke(id: String) {
        validator.checkNettingId(id)
        nettingReportRepository.deleteById(id)
        nettingCycleRepository.deleteById(id)
    }
}