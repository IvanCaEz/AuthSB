package com.ivancaez.auth.domain.dto

import com.ivancaez.auth.domain.Role

data class UserDto(
    val id: Long?,
    val username: String,
    val email: String,
    val image: String,
    val password: String,
    val role: Role
)
