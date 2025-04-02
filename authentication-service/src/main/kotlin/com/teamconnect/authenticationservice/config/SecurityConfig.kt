@file:Suppress("DEPRECATION")

package com.teamconnect.authenticationservice.config

import com.teamconnect.authenticationservice.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.web.invoke

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Use @EnableMethodSecurity instead of @EnableGlobalMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http { // Start lambda DSL
            cors { } // Simplified CORS enabling
            csrf { disable() } // Disable CSRF
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS } // Stateless sessions
            authorizeHttpRequests { // New authorization block
                authorize("/signup", permitAll) // Permit access to /signup
                authorize("/login", permitAll) // Permit access to /login
                authorize(anyRequest, authenticated) // Require authentication for any other request
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter) // Add JWT filter
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}