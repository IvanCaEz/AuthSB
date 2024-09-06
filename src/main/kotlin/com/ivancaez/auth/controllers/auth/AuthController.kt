package com.ivancaez.auth.controllers.auth

import com.ivancaez.auth.domain.auth.AuthRequest
import com.ivancaez.auth.domain.auth.AuthResponse
import com.ivancaez.auth.services.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val authResponse = authenticationService.authenticate(authRequest)
        return ResponseEntity(authResponse, HttpStatus.OK)
    }
}