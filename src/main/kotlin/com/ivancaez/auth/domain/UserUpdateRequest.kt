package com.ivancaez.auth.domain

data class UserUpdateRequest(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = null,
    val password: String? = null
)
