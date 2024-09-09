package com.ivancaez.auth.services

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService(private val userService: UserService) {

    fun canAccessUserId(id: Long): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUser = userService.getUserByEmail(authentication.name)
        return currentUser?.id == id || authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }
}