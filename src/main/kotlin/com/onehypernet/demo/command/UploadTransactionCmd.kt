package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.AppFormatter
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.helper.AmountConverterImpl
import com.onehypernet.demo.model.entity.NettedTransactionEntity
import com.onehypernet.demo.model.entity.NettingReportEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.enumerate.TransactionType
import com.onehypernet.demo.model.factory.UploadedTransactionFactory
import com.onehypernet.demo.repository.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import javax.transaction.Transactional

@Service
open class UploadTransactionCmd(
    private val validator: Validator,
    private val uploadedTransactionFactory: UploadedTransactionFactory,
    private val transactionFileRepository: TransactionFileRepository,
    private val nettedTransactionRepository: NettedTransactionRepository,
    private val nettingCycleRepository: NettingCycleRepository,
    private val userRepository: UserRepository,
    private val nettingParamRepository: NettingParamRepository,
    private val nettingReportRepository: NettingReportRepository,
    private val appFormatter: AppFormatter
) {
    @Transactional
    open operator fun invoke(file: MultipartFile, userId: String, nettingId: String) {
        val user = userRepository.findById(userId).get()
        val ourCurrency = user.detail?.currency ?: AppConst.BRIDGING_CURRENCY

        validator.checkNettingId(nettingId)

        val nettingCycle = nettingCycleRepository.requireById(nettingId)
        validator.requireOpening(nettingCycle)

        val transactions = uploadedTransactionFactory.createList(file.inputStream)

        val converter = AmountConverterImpl(ourCurrency, nettingParamRepository.findAllByToday())

        val nettedTrans = ArrayList<NettedTransactionEntity>(transactions.size)

        var receiveAmount = BigDecimal(0.0)
        var receiveTransactions = 0
        val counterPartyReceive = hashSetOf<String>()

        var payAmount = BigDecimal(0.0)
        var payTransactions = 0
        val counterPartyPay = hashSetOf<String>()
        val counterParty = hashSetOf<String>()
        val currencies = hashSetOf<String>()

        var totalFeeBefore = BigDecimal(0.0)
        var totalCashBefore = BigDecimal(0.0)

        transactions.forEach {
            val localAmount = converter.getLocal(it.amount, it.currency)
            val feeAmount = converter.getFee(it.amount, it.currency)

            if (it.type == TransactionType.Payable) {
                payAmount += localAmount
                payTransactions++
                counterPartyPay.add(it.counterPartyName)
                totalFeeBefore += feeAmount
            } else {
                receiveAmount += localAmount
                receiveTransactions++
                counterPartyReceive.add(it.counterPartyName)
            }
            counterParty.add(it.counterPartyName)
            currencies.add(it.currency)
            totalCashBefore += localAmount

            nettedTrans.add(
                NettedTransactionEntity(
                    id = it.id,
                    userId = userId,
                    counterParty = it.counterPartyName,
                    amount = it.amount,
                    localAmount = localAmount,
                    feeAmount = feeAmount,
                    currency = it.currency,
                    transactionType = it.type,
                    nettingId = nettingId,
                    date = it.date,
                    dueDate = it.dueDate,
                )
            )
        }
        val totalFeeAfter = converter.generateTotalFeeAfter(totalFeeBefore)
        val totalCashAfter = converter.generateTotalCashAfter(totalFeeBefore)

        val report = NettingReportEntity(
            nettingId = nettingId,
            userId = userId,
            receiveAmount = appFormatter.formatAmount(receiveAmount),
            receiveTransactions = receiveTransactions,
            numOfCounterPartyReceive = counterPartyReceive.size,
            payAmount = appFormatter.formatAmount(payAmount),
            payTransactions = payTransactions,
            numOfCounterPartyPay = counterPartyPay.size,
            numOfTransactionAfter = 1,
            numOfCounterParty = counterParty.size,
            numOfCurrencyBefore = currencies.size,
            numOfCurrencyAfter = 1,
            totalFeeBefore = appFormatter.formatAmount(totalFeeBefore),
            totalFeeAfter = appFormatter.formatAmount(totalFeeAfter),
            totalCashBefore = appFormatter.formatAmount(totalCashBefore),
            totalCashAfter = appFormatter.formatAmount(totalCashAfter),
        )
        nettingReportRepository.save(report)
        nettedTransactionRepository.saveAll(nettedTrans)
        transactionFileRepository.save(userId, nettingId, file)
        nettingCycleRepository.save(nettingCycle.copy(status = NettingStatus.InProgress))
    }
}