package com.onehypernet.demo.model.entity

import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity(name = "netting_param")
class NettingParamEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: String = "",

    @Column(name = "from_currency")
    var fromCurrency: String = "",

    @Column(name = "to_currency")
    var toCurrency: String = "",

    @Column(name = "margin_percent")
    var margin: Double = 0.0,

    @Column(name = "fee_percent")
    var fee: Double = 0.0,

    @Column(name = "min_fee")
    var minFee: Double = 0.0,

    @Column(name = "max_fee")
    var maxFee: Double = 0.0,

    @Column(name = "fixed_fee")
    var fixedFee: Double = 0.0,

    @Column(name = "exchange_rate")
    var exchangeRate: Double = 1.0,

    @Column(name = "at_location")
    var atLocationCode: String = "",

    @Column(name = "destination_locations")
    var destinationLocations: String = "",

    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now(),
)