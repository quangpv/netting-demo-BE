package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.datasource.NettingCycleDao
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.helper.ReportCalculatorImpl
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.response.*
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.NettingReportRepository
import com.onehypernet.demo.repository.TransactionFileRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GetNettingCycleByIdCmd(
    private val nettingCycleDao: NettingCycleDao,
    private val nettedTransactionRepository: NettedTransactionRepository,
    private val userRepository: UserRepository,
    private val appFormatter: AppFormatter,
    private val fileRepository: TransactionFileRepository,
    private val nettingReportRepository: NettingReportRepository
) {
    operator fun invoke(userId: String, nettingId: String): NettingCycleDetailResponse {
        val netting = nettingCycleDao.findByIdOrNull(nettingId) ?: throws("Not found netting id $nettingId")
        val report = nettingReportRepository.findByIdOrNull(nettingId)
        if (report != null && report.userId != userId)
            throws("You don't have permission to access netting id $netting")

        val nettedTransactions = nettedTransactionRepository.findAllByNettingIdAndUserId(nettingId, userId)
        val user = userRepository.findById(userId).get().detail
        val ourCurrency = user?.currency ?: AppConst.BRIDGING_CURRENCY
        val reportCalculator = ReportCalculatorImpl()

        val uploadedFileEntity = fileRepository.findByUserAndNetting(userId, nettingId)
        val uploadedFile = uploadedFileEntity?.let { fileRepository.getFile(userId, it.storedFileName) }

        val uploadedFileResponse = if (uploadedFileEntity != null && uploadedFile != null) {
            FileResponse(
                uploadedFileEntity.fileName,
                uploadedFile.length(),
                uploadedFile.extension,
                uploadedFileEntity.storedFileName
            )
        } else null
        val reportFile = uploadedFileResponse?.let {
            FileResponse("$nettingId.pdf", 0, "pdf", "$nettingId.pdf")
        }

        val response = NettingCycleDetailResponse(
            id = nettingId,
            group = netting.nettingGroup,
            status = netting.status,
            currency = ourCurrency,
            createAt = appFormatter.formatDate(netting.createAt),
        )
        if (netting.status == NettingStatus.None
            || netting.status == NettingStatus.Open
            || report == null
        ) {
            return response
        }
        val receivable = NettedReportResponse(
            report.receiveAmount,
            report.receiveTransactions,
            report.numOfCounterPartyReceive
        )
        val payable = NettedReportResponse(
            report.payAmount,
            report.payTransactions,
            report.numOfCounterPartyPay
        )
        val transactionCount = report.payTransactions + report.receiveTransactions
        val cashFlow = NettedReportResponse(
            report.receiveAmount - report.payAmount,
            transactionCount,
            report.numOfCounterParty
        )
        val savingFeePercent = reportCalculator.calculateSavingPercent(report.totalFeeBefore, report.totalFeeAfter)

        val savingFee = EstimatedSavingResponse(
            before = report.totalFeeBefore,
            after = report.totalFeeAfter,
            savingAmount = report.totalFeeBefore - report.totalFeeAfter,
            savingPercent = appFormatter.formatPercent(savingFeePercent)
        )

        val totalCashBefore = payable.amount
        val totalCashAfter = payable.amount - receivable.amount
        val savingCashPercent = reportCalculator.calculateSavingPercent(totalCashBefore, totalCashAfter)

        val savingCash = EstimatedSavingResponse(
            before = totalCashBefore,
            after = totalCashAfter,
            savingAmount = totalCashBefore - totalCashAfter,
            savingPercent = appFormatter.formatPercent(savingCashPercent)
        )

        val summary = SummaryReportResponse(
            transactions = BeforeAfterResponse(
                BigDecimal(transactionCount),
                BigDecimal(report.numOfTransactionAfter)
            ),
            currencies = BeforeAfterResponse(
                BigDecimal(report.numOfCurrencyBefore),
                BigDecimal(report.numOfTransactionAfter)
            ),
            fees = BeforeAfterResponse(
                report.totalFeeBefore,
                report.totalFeeAfter
            ),
            cashOutFlow = BeforeAfterResponse(
                totalCashBefore,
                totalCashAfter
            )
        )
        val settlementDate = if (netting.status == NettingStatus.Settled)
            appFormatter.formatDate(netting.updateAt)
        else null

        return response.copy(
            settlementDate = settlementDate,
            uploadedFile = uploadedFileResponse,
            reportFile = reportFile,
            receivable = receivable,
            payable = payable,
            netCashFlow = cashFlow,
            savingFee = savingFee,
            savingCash = savingCash,
            potential = reportCalculator.getPotential(
                savingCash.savingPercent,
                savingFee.savingPercent,
                transactionCount
            ),
            summary = summary,
            nettedTransactions = nettedTransactions.map {
                NettedTransactionResponse(
                    date = appFormatter.formatDate(it.date),
                    dueDate = appFormatter.formatDate(it.dueDate),
                    transactionId = it.id,
                    type = it.transactionType,
                    counterParty = it.counterParty,
                    billAmount = Amount(it.currency, it.amount),
                    localAmount = Amount(ourCurrency, it.localAmount),
                    feeSaved = Amount(ourCurrency, it.feeAmount),
                )
            }
        )
    }
}