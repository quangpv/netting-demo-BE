package com.onehypernet.demo.model.entity

import com.onehypernet.demo.model.enumerate.NettingStatus
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "netting_cycle")
data class NettingCycleEntity(
    @Id
    var id: String = "",
    @Column(name = "netting_group")
    var nettingGroup: String = "",
    var status: NettingStatus = NettingStatus.None,
    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now()
)