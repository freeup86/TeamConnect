package com.teamconnect.authenticationservice.service

import com.teamconnect.authenticationservice.repository.UserRepository
import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val log = logger()

    override fun loadUserByUsername(username: String): UserDetails {
        log.debug("Authenticating $username")

        val user = userRepository.findByEmail(username)
            .orElseThrow { ResourceNotFoundException("User not found with email: $username") }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return User(
            user.email,
            user.password,
            authorities
        )
    }
}