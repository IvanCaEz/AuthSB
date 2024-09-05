package com.ivancaez.auth.domain.dto

data class UserDto(
    val id: Long?,
    val username: String,
    val email: String,
    val image: String,
    val password: String
)
