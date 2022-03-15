package com.onehypernet.demo.repository

import com.onehypernet.demo.model.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<UserEntity, String> {

    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    fun findByEmail(email: String): UserEntity?
}