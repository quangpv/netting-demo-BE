package com.onehypernet.demo.command

import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.model.entity.NettedTransactionEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import com.onehypernet.demo.model.factory.UploadedTransactionFactory
import com.onehypernet.demo.repository.NettedTransactionRepository
import com.onehypernet.demo.repository.NettingCycleRepository
import com.onehypernet.demo.repository.TransactionFileRepository
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional

@Service
open class UploadTransactionCmd(
    private val validator: Validator,
    private val uploadedTransactionFactory: UploadedTransactionFactory,
    private val transactionFileRepository: TransactionFileRepository,
    private val nettedTransactionRepository: NettedTransactionRepository,
    private val nettingCycleRepository: NettingCycleRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    open operator fun invoke(file: MultipartFile, userId: String, nettingId: String) {
        validator.checkNettingId(nettingId)

        val nettingCycle = nettingCycleRepository.requireById(nettingId)
        validator.requireOpening(nettingCycle)

        val transactions = uploadedTransactionFactory.createList(file.inputStream)
        val nettedTransactions = nettedTransactionRepository.findAllById(transactions.map { it.id })

        validator.requireNotExists(nettedTransactions)

        nettedTransactionRepository.saveAll(transactions.map {
            NettedTransactionEntity(
                id = it.id,
                userId = userId,
                counterParty = it.counterPartyName,
                amount = it.amount,
                currency = it.currency,
                transactionType = it.type,
                nettingId = nettingId,
                date = it.date,
                dueDate = it.dueDate
            )
        })
        transactionFileRepository.save(userId, nettingId, file)
        nettingCycleRepository.save(nettingCycle.copy(status = NettingStatus.InProgress))
    }
}