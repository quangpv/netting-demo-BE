package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.TextFormatter
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.helper.ReportCalculator
import com.onehypernet.demo.model.enumerate.TransactionType
import com.onehypernet.demo.model.response.*
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.ReportParamRepository
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
    private val fileRepository: TransactionFileRepository,
    private val reportCalculator: ReportCalculator,
    private val reportParamRepository: ReportParamRepository
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

        val receivableCounterParty = hashSetOf<String>()
        val payableCounterParty = hashSetOf<String>()
        val counterParties = hashSetOf<String>()
        val currencies = hashSetOf<String>()
        val transactionCount = nettedTransactions.size

        val receivable = NettedReportResponse()
        val payable = NettedReportResponse()
        val transactionList = ArrayList<NettedTransactionResponse>(nettedTransactions.size)
        val savingFee = EstimatedSavingResponse()
        val savingCash = EstimatedSavingResponse()
        val reportParam = reportParamRepository.findOrCreate(nettingId)
        val summary = SummaryReportResponse()

        nettedTransactions.forEach {
            val localAmount = reportCalculator.getLocalAmount(it)
            val feeAmount = reportCalculator.getFeeAmount(it)

            if (it.transactionType == TransactionType.Receivable) {
                receivable.amount += localAmount
                receivable.numOfTransactions += 1
                receivableCounterParty.add(it.counterParty)
            } else {
                payable.amount += localAmount
                payable.numOfTransactions += 1
                payableCounterParty.add(it.counterParty)
                savingFee.before += feeAmount
            }
            counterParties.add(it.counterParty)

            transactionList.add(
                NettedTransactionResponse(
                    date = textFormatter.formatDate(it.date),
                    dueDate = textFormatter.formatDate(it.dueDate),
                    transactionId = it.id,
                    type = it.transactionType,
                    counterParty = it.counterParty,
                    billAmount = Amount(it.currency, it.amount),
                    localAmount = Amount(ourCurrency, localAmount),
                    feeSaved = Amount(ourCurrency, feeAmount)
                )
            )
        }
        receivable.numOfCounterParties = receivableCounterParty.size
        payable.numOfCounterParties = payableCounterParty.size
        savingCash.before = payable.amount

        with(savingFee) {
            after = reportCalculator.getFeeAfterAmount(before, reportParam)
            savingAmount = reportCalculator.getSavingAmount(before, after)
            savingPercent = reportParam.savingFeePercent
        }

        with(savingCash) {
            after = reportCalculator.getCashAfterAmount(before, reportParam)
            savingAmount = reportCalculator.getSavingAmount(before, after)
            savingPercent = reportParam.savingCashPercent
        }

        val netCashFlow = NettedReportResponse(
            receivable.amount - payable.amount,
            transactionCount,
            counterParties.size
        )
        val potential = reportCalculator.getPotential(savingCash.savingAmount, savingFee.savingAmount, transactionCount)
        summary.currencies.apply {
            before = BigDecimal(currencies.size)
            after = BigDecimal(1.0)
        }
        summary.transactions.apply {
            before = BigDecimal(transactionCount)
            after = BigDecimal(1.0)
        }
        summary.fees.apply {
            before = savingFee.before
            after = savingFee.after
        }
        summary.cashOutFlow.apply {
            before = savingCash.before
            after = savingCash.after
        }

        return NettingCycleDetailResponse(
            id = nettingId,
            group = netting.nettingGroup,
            status = netting.status,
            currency = ourCurrency,
            createAt = textFormatter.formatDate(netting.createAt),
            settlementDate = null,
            uploadedFile = uploadedFileResponse,
            reportFile = FileResponse("$nettingId.pdf", 0, "pdf", "$nettingId.pdf"),
            receivable = receivable,
            payable = payable,
            netCashFlow = netCashFlow,
            savingFee = savingFee,
            savingCash = savingCash,
            potential = potential,
            summary = summary,
            nettedTransactions = transactionList
        )
    }
}