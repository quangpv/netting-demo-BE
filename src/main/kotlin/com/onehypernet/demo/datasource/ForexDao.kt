package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.ForexEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface ForexDao : JpaRepository<ForexEntity, String> {
    @Query(
        "delete from forex where FORMATDATETIME(curdate(),'yyyyMMdd') = FORMATDATETIME(create_at,'yyyyMMdd')",
        nativeQuery = true
    )
    @Modifying
    @Transactional
    fun removeAllToDay()
}