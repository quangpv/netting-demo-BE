package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.MarginEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MarginRepository : JpaRepository<MarginEntity, String>