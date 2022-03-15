package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.TextFormatter
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.response.*
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.TransactionFileRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigDecimal

@Service
class GetNettingCycleByIdCmd(
    private val nettingCycleDao: NettingCycleDao,
    private val nettedTransactionRepository: NettedTransactionRepository,
    private val userRepository: UserRepository,
    private val textFormatter: TextFormatter,
    private val fileRepository: TransactionFileRepository
) {
    operator fun invoke(userId: String, nettingId: String): NettingCycleDetailResponse {
        val netting = nettingCycleDao.findByIdOrNull(nettingId) ?: throws("Not found netting id $nettingId")
        val nettedTransactions = nettedTransactionRepository.findAllByNettingId(nettingId)
        val user = userRepository.findById(userId).get().detail
        val ourCurrency = user?.currency ?: AppConst.BRIDGING_CURRENCY

        val uploadedFileEntity = fileRepository.findByUserAndNetting(userId, nettingId)
        val uploadedFile = uploadedFileEntity?.let { File(it.storedFileName) }

        val uploadedFileResponse = if (uploadedFileEntity != null && uploadedFile != null) {
            FileResponse(
                uploadedFileEntity.fileName,
                uploadedFile.length(),
                uploadedFile.extension,
                uploadedFileEntity.storedFileName
            )
        } else null

        return NettingCycleDetailResponse(
            id = nettingId,
            group = netting.nettingGroup,
            status = netting.status,
            currency = ourCurrency,
            createAt = textFormatter.formatDate(netting.createAt),
            settlementDate = null,
            uploadedFile = uploadedFileResponse,
            reportFile = FileResponse("$nettingId.pdf", 0, "pdf", "$nettingId.pdf"),
            receivable = NettedReportResponse(BigDecimal(0.0), 0, 0),
            payable = NettedReportResponse(BigDecimal(0.0), 0, 0),
            netCashFlow = NettedReportResponse(BigDecimal(0.0), 0, 0),
            savingFee = EstimatedSavingResponse(),
            savingCash = EstimatedSavingResponse(),
            potential = 0.0,
            summary = SummaryReportResponse(),
            nettedTransactions = nettedTransactions.map {
                NettedTransactionResponse(
                    date = textFormatter.formatDate(it.date),
                    dueDate = textFormatter.formatDate(it.dueDate),
                    transactionId = it.id,
                    type = it.transactionType,
                    counterParty = it.counterParty,
                    billAmount = Amount(it.currency, it.amount),
                    localAmount = Amount(ourCurrency, it.amount),
                    feeSaved = Amount(ourCurrency, 0.0.toBigDecimal())
                )
            }
        )
    }
}