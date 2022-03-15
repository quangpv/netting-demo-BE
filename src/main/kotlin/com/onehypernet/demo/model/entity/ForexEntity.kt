package com.onehypernet.demo.model.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "forex")
class ForexEntity(
    @Id
    var id: String = "",

    @Column(name = "exchange_rate")
    var exchangeRate: Double = 0.0,

    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now()
)