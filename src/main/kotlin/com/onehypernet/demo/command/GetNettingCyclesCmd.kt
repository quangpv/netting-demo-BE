package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.extension.safe
import com.onehypernet.demo.model.entity.NettingCycleEntity
import com.onehypernet.demo.model.entity.NettingReportEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.enumerate.SettleStatus
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.PagingRequest
import com.onehypernet.demo.model.response.Amount
import com.onehypernet.demo.model.response.ListResponse
import com.onehypernet.demo.model.response.MetadataResponse
import com.onehypernet.demo.model.response.NettingCycleResponse
import com.onehypernet.demo.repository.NettingReportRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.streams.toList

@Service
class GetNettingCyclesCmd(
    private val nettingCycleDao: NettingCycleDao,
    private val nettingReportRepository: NettingReportRepository,
    private val userRepository: UserRepository,
    private val appFormatter: AppFormatter
) {
    operator fun invoke(request: PagingRequest, userId: String): ListResponse<NettingCycleResponse> {
        val user = userRepository.findById(userId).get()
        val localCurrency = user.detail?.currency ?: AppConst.BRIDGING_CURRENCY
        val result = if (user.role == UserRole.Admin) nettingCycleDao.findAll(request.toPageRequest())
        else nettingCycleDao.findAllByUser(userId, request.toPageRequest())

        val data = result.stream().map {
            val report = nettingReportRepository.findByIdOrNull(it.id)
            create(localCurrency, it, report)
        }.toList()

        val pageable = result.pageable
        return ListResponse(
            data,
            MetadataResponse(
                pageable.pageNumber + 1,
                result.totalElements.toInt()
            )
        )
    }

    private fun create(
        localCurrency: String,
        entity: NettingCycleEntity,
        report: NettingReportEntity?
    ): NettingCycleResponse {
        return NettingCycleResponse(
            id = entity.id,
            group = entity.nettingGroup,
            status = entity.status,
            createAt = appFormatter.formatDate(entity.createAt),
            settlementDate = if (entity.status == NettingStatus.Settled)
                appFormatter.formatDate(entity.updateAt)
            else null,
            receivable = Amount(localCurrency, report?.receiveAmount ?: BigDecimal(0.0)),
            payable = Amount(localCurrency, report?.payAmount ?: BigDecimal(0.0)),
            transactionCount = report?.let {
                it.payTransactions + it.receiveTransactions
            } ?: 0,
            savingFee = Amount(localCurrency, report?.let {
                it.totalFeeBefore - it.totalFeeAfter
            }.safe(0.0)),
            savingCash = Amount(localCurrency, report?.let {
                it.totalCashBefore - it.totalCashAfter
            }.safe(0.0))
        )
    }
}
