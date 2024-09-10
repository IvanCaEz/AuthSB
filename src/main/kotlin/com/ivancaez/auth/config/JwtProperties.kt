package com.ivancaez.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val expirationMs: Long,
    val refreshTokenExpirationMs: Long
)
