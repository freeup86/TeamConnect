package com.teamconnect.authenticationservice.dto

import com.teamconnect.authenticationservice.model.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val id: Long,
    val name: String,
    val email: String,
    val role: Role
)

data class UserSummaryDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role
)