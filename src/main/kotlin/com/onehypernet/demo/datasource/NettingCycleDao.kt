package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.NettingCycleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NettingCycleDao : JpaRepository<NettingCycleEntity, String> {

}