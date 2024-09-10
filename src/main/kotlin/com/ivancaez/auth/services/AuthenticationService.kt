package com.ivancaez.auth.services

import com.ivancaez.auth.config.JwtProperties
import com.ivancaez.auth.domain.auth.AuthRequest
import com.ivancaez.auth.domain.auth.AuthResponse
import com.ivancaez.auth.repositories.RefreshTokenRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun authenticate(authRequest: AuthRequest): AuthResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.email,
                authRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authRequest.email)
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user)

        refreshTokenRepository.save(refreshToken, user)

        return AuthResponse(accessToken, refreshToken)

    }
    fun refreshToken(token: String): String? {
        val extractedEmail = tokenService.extractEmail(token)
        return extractedEmail?.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(token)
            if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails?.username) {
                generateAccessToken(currentUserDetails)
            } else {
                null
            }
        }
    }
    private fun generateRefreshToken(user: UserDetails) =
        tokenService.generate(user, Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpirationMs))

    private fun generateAccessToken(user: UserDetails) =
        tokenService.generate(user, Date(System.currentTimeMillis() + jwtProperties.expirationMs))




}
