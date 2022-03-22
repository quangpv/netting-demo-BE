package com.onehypernet.demo.model.entity

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "margin")
class MarginEntity(
    @Id
    var id: String = "",
    var percent: BigDecimal = BigDecimal(0.0)
)