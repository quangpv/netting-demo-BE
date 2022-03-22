package com.onehypernet.demo.model.entity

import com.onehypernet.demo.model.enumerate.TransactionType
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity(name = "netted_transaction")
@IdClass(TransactionId::class)
class NettedTransactionEntity(
    @Id
    var id: String = "",

    @Id
    @Column(name = "user_id")
    var userId: String = "",

    @Id
    @Column(name = "netting_id")
    var nettingId: String = "",

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

    var date: LocalDateTime = LocalDateTime.now(),

    @Column(name = "due_date")
    var dueDate: LocalDateTime = LocalDateTime.now()
)

class TransactionId(
    var id: String = "",
    var userId: String = "",
    var nettingId: String = "",
) : Serializable