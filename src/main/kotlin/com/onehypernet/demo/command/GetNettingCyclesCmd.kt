package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.helper.ReportCalculatorImpl
import com.onehypernet.demo.model.factory.NettingCycleFactory
import com.onehypernet.demo.model.request.PagingRequest
import com.onehypernet.demo.model.response.ListResponse
import com.onehypernet.demo.model.response.MetadataResponse
import com.onehypernet.demo.model.response.NettingCycleResponse
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.NettingParamRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class GetNettingCyclesCmd(
    private val nettingCycleDao: NettingCycleDao,
    private val nettingCycleFactory: NettingCycleFactory,
    private val nettedTransactionRepository: NettedTransactionRepository,
    private val nettingParamRepository: NettingParamRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(request: PagingRequest, userId: String): ListResponse<NettingCycleResponse> {
        val result = nettingCycleDao.findAll(request.toPageRequest())
        val user = userRepository.findById(userId).get()
        val localCurrency = user.detail?.currency ?: AppConst.BRIDGING_CURRENCY
        val calculator = ReportCalculatorImpl(
            localCurrency,
            nettingParamRepository.findAllByToday(),
        )
        val data = result.stream().map {
            val transactions = nettedTransactionRepository.findAllByNettingId(it.id)
            nettingCycleFactory.create(localCurrency, it, transactions, calculator)
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
}