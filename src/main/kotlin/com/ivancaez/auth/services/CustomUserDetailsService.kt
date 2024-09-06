package com.ivancaez.auth.services

import com.ivancaez.auth.mapToUserDetails
import com.ivancaez.auth.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService (
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)
            ?.mapToUserDetails()
            ?: throw IllegalArgumentException("User not found.")
    }

}