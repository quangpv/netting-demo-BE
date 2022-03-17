package com.onehypernet.demo.command

import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.request.SettleRequest
import com.onehypernet.demo.repository.NettingCycleRepository
import com.onehypernet.demo.repository.NettingReportRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
open class SettleCmd(
    private val nettingReportRepository: NettingReportRepository,
    private val nettingCycleRepository: NettingCycleRepository,
) {
    @Transactional
    open operator fun invoke(userId: String, nettingId: String, request: SettleRequest) {
        val report = nettingReportRepository.findByIdOrNull(nettingId)
            ?: throws("Netting $nettingId not found to settlement")
        if (report.userId != userId) {
            throws("You're not in netting $nettingId")
        }
        val netting = nettingCycleRepository.requireById(nettingId)

        if (netting.status <= NettingStatus.Open)
            throws("Netting $nettingId is not ready to settle")

        if (netting.status == NettingStatus.Settled)
            throws("Netting $nettingId has been Settled")

        nettingCycleRepository.save(
            netting.copy(
                status = NettingStatus.Settled,
                updateAt = LocalDateTime.now()
            )
        )
    }
}