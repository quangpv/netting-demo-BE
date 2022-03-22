package com.onehypernet.demo.model.entity

import com.onehypernet.demo.model.enumerate.NettingStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "netting_cycle")
data class NettingCycleEntity(
    @Id
    var id: String = "",

    @Column(name = "netting_group")
    var nettingGroup: String = "",

    var status: NettingStatus = NettingStatus.None,

    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "update_at")
    var updateAt: LocalDateTime = LocalDateTime.now(),

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @PrimaryKeyJoinColumn(referencedColumnName = "id")
    var report: NettingReportEntity? = null
)