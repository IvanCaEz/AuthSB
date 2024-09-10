package com.ivancaez.auth.domain.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
