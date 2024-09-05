package com.ivancaez.auth.domain.dto

data class UserUpdateRequestDto(
    val id: Long?,
    val username: String?,
    val email: String?,
    val image: String?,
    val password: String?
)
