package com.teamconnect.authenticationservice.service

import com.teamconnect.authenticationservice.dto.AuthResponse
import com.teamconnect.authenticationservice.dto.LoginRequest
import com.teamconnect.authenticationservice.dto.SignupRequest
import com.teamconnect.authenticationservice.dto.UserSummaryDto
import com.teamconnect.authenticationservice.model.Role
import com.teamconnect.authenticationservice.model.User
import com.teamconnect.authenticationservice.repository.UserRepository
import com.teamconnect.authenticationservice.security.JwtTokenProvider
import com.teamconnect.common.exception.BadRequestException
import com.teamconnect.common.exception.ResourceAlreadyExistsException
import com.teamconnect.common.exception.UnauthorizedException
import com.teamconnect.common.util.logger
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {
    private val log = logger()

    fun signup(signupRequest: SignupRequest): AuthResponse {
        log.info("Processing signup for user: ${signupRequest.email}")

        if (userRepository.existsByEmail(signupRequest.email)) {
            throw ResourceAlreadyExistsException("Email is already in use")
        }

        val user = User(
            name = signupRequest.name,
            email = signupRequest.email,
            password = passwordEncoder.encode(signupRequest.password)
        )

        val savedUser = userRepository.save(user)

        val jwt = jwtTokenProvider.generateToken(
            savedUser.id,
            savedUser.email,
            savedUser.role
        )

        return AuthResponse(
            token = jwt,
            id = savedUser.id,
            name = savedUser.name,
            email = savedUser.email,
            role = savedUser.role
        )
    }

    fun login(loginRequest: LoginRequest): AuthResponse {
        log.info("Processing login for user: ${loginRequest.email}")

        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
                )
            )

            SecurityContextHolder.getContext().authentication = authentication

            val user = userRepository.findByEmail(loginRequest.email)
                .orElseThrow { BadRequestException("User not found") }

            val jwt = jwtTokenProvider.generateToken(
                user.id,
                user.email,
                user.role
            )

            return AuthResponse(
                token = jwt,
                id = user.id,
                name = user.name,
                email = user.email,
                role = user.role
            )
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid email or password")
        }
    }

    fun getCurrentUser(): UserSummaryDto {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthorizedException("User not authenticated")

        val user = userRepository.findByEmail(authentication.name)
            .orElseThrow { BadRequestException("User not found") }

        return UserSummaryDto(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role
        )
    }

    fun getUserById(id: Long): UserSummaryDto {
        val user = userRepository.findById(id)
            .orElseThrow { BadRequestException("User not found with id: $id") }

        return UserSummaryDto(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role
        )
    }
}