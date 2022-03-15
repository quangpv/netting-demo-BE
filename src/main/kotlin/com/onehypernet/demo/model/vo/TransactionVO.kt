package com.onehypernet.demo.model.vo

import com.onehypernet.demo.model.enumerate.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class TransactionVO(
    var id: String = "",
    var type: TransactionType = TransactionType.None,
    var counterPartyName: String = "",
    var counterPartyId: String = "",
    var currency: String = "",
    var amount: BigDecimal = 0.0.toBigDecimal(),
    var dueDate: LocalDateTime = LocalDateTime.now(),
    var date: LocalDateTime = LocalDateTime.now(),
)