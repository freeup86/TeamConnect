package com.teamconnect.authenticationservice.controller

import com.teamconnect.authenticationservice.dto.AuthResponse
import com.teamconnect.authenticationservice.dto.LoginRequest
import com.teamconnect.authenticationservice.dto.SignupRequest
import com.teamconnect.authenticationservice.dto.UserSummaryDto
import com.teamconnect.authenticationservice.service.AuthService
import com.teamconnect.common.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class AuthController(
    private val authService: AuthService
) {
    private val log = logger()

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<AuthResponse> {
        log.info("Received signup request for: ${signupRequest.email}")
        val response = authService.signup(signupRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        log.info("Received login request for: ${loginRequest.email}")
        val response = authService.login(loginRequest)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserSummaryDto> {
        log.info("Getting current user details")
        val userDetails = authService.getCurrentUser()
        return ResponseEntity.ok(userDetails)
    }

    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserSummaryDto> {
        log.info("Getting user details for id: $id")
        val userDetails = authService.getUserById(id)
        return ResponseEntity.ok(userDetails)
    }

    @GetMapping("/users/{id}/basic")
    fun getUserBasicInfo(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        log.info("Getting basic user info for id: $id")
        val userDetails = authService.getUserById(id)

        val basicInfo = mapOf(
            "id" to userDetails.id,
            "name" to userDetails.name,
            "email" to userDetails.email
        )

        return ResponseEntity.ok(basicInfo)
    }
}