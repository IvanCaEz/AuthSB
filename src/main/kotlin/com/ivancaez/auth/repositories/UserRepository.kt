package com.ivancaez.auth.repositories

import com.ivancaez.auth.domain.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Long?> {
    fun findByEmail(email: String): UserEntity?
}