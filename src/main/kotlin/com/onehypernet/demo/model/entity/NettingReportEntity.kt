package com.onehypernet.demo.model.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "netting_report")
data class NettingReportEntity(

    @Id
    @Column(name = "id")
    var nettingId: String = "",

    @Column(name = "user_id")
    var userId: String = "",

    @Column(name = "receive_amount")
    var receiveAmount: BigDecimal = BigDecimal(0.0),
    @Column(name = "receive_transactions")
    var receiveTransactions: Int = 0,

    @Column(name = "num_of_counter_party_receive")
    var numOfCounterPartyReceive: Int = 0,

    @Column(name = "pay_amount")
    var payAmount: BigDecimal = BigDecimal(0.0),
    @Column(name = "pay_transactions")
    var payTransactions: Int = 0,
    @Column(name = "num_of_counter_party_pay")
    var numOfCounterPartyPay: Int = 0,

    @Column(name = "transactions_after")
    var numOfTransactionAfter: Int = 0,
    @Column(name = "num_of_counter_party")
    var numOfCounterParty: Int = 0,
    @Column(name = "num_of_currency_before")
    var numOfCurrencyBefore: Int = 0,
    @Column(name = "num_of_currency_after")
    var numOfCurrencyAfter: Int = 0,

    @Column(name = "total_fee_before")
    var totalFeeBefore: BigDecimal = BigDecimal(0.0),
    @Column(name = "total_fee_after")
    var totalFeeAfter: BigDecimal = BigDecimal(0.0),

    @Column(name = "total_cash_before")
    var totalCashBefore: BigDecimal = BigDecimal(0.0),
    @Column(name = "total_cash_after")
    var totalCashAfter: BigDecimal = BigDecimal(0.0),

    @Column(name = "settle_date")
    var settledDate: LocalDateTime? = null,
    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "update_at")
    var updateAt: LocalDateTime = LocalDateTime.now(),
)