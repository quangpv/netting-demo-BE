package com.onehypernet.demo.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "report_param")
class ReportParamEntity(

    @Id
    @Column(name = "netting_id")
    var nettingId: String = "",

    @Column(name = "saving_fee_percent")
    var savingFeePercent: Double = 0.0,

    @Column(name = "saving_cash_percent")
    var savingCashPercent: Double = 0.0,
)