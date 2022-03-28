package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.entity.NettingCycleEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NettingCycleDao : JpaRepository<NettingCycleEntity, String> {
    companion object {
        const val FIND_ALL_BY_USER = """
            select * from NETTING_CYCLE netting where 
                netting.STATUS = 1 or 
                ( netting.STATUS > 1 and 
                exists(select ID from NETTING_REPORT report where report.ID = netting.ID and USER_ID=?1) )
        """
    }

    @Query(
        value = FIND_ALL_BY_USER,
        countQuery = FIND_ALL_BY_USER,
        nativeQuery = true
    )
    fun findAllByUser(userId: String, pageable: Pageable): Page<NettingCycleEntity>

    @Query("select * from NETTING_CYCLE order by CREATE_AT desc limit 1", nativeQuery = true)
    fun findLast(): NettingCycleEntity?
}