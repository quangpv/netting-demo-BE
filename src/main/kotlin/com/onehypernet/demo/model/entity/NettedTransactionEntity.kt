package com.onehypernet.demo.model.entity

import com.onehypernet.demo.model.enumerate.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "netted_transaction")
class NettedTransactionEntity(
    @Id
    var id: String = "",

    @Column(name = "user_id")
    var userId: String = "",

    @Column(name = "counter_party")
    var counterParty: String = "",

    var amount: BigDecimal = 0.0.toBigDecimal(),

    var currency: String = "",

    @Column(name = "local_amount")
    var localAmount: BigDecimal = 0.0.toBigDecimal(),

    @Column(name = "fee_amount")
    var feeAmount: BigDecimal = 0.0.toBigDecimal(),

    @Column(name = "transaction_type")
    var transactionType: TransactionType = TransactionType.None,

    @Column(name = "netting_id")
    var nettingId: String = "",

    var date: LocalDateTime = LocalDateTime.now(),

    @Column(name = "due_date")
    var dueDate: LocalDateTime = LocalDateTime.now()
)