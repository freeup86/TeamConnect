package com.teamconnect.authenticationservice.security

import com.teamconnect.authenticationservice.model.Role
import com.teamconnect.common.security.JwtConfig
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwt.expiration}")
    private var jwtExpirationMs: Long = 0

    @Value("\${app.jwt.token-prefix}")
    private lateinit var tokenPrefix: String

    @Value("\${app.jwt.header-name}")
    private lateinit var headerName: String

    private lateinit var jwtConfig: JwtConfig

    @PostConstruct
    fun init() {
        jwtConfig = JwtConfig(jwtSecret, jwtExpirationMs, tokenPrefix, headerName)
    }

    fun generateToken(userId: Long, email: String, role: Role): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(email)
            .claim(JwtConfig.USER_ID_CLAIM, userId)
            .claim(JwtConfig.ROLE_CLAIM, role.name)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return claims.get(JwtConfig.USER_ID_CLAIM, Long::class.java)
    }

    fun getEmailFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun getRoleFromToken(token: String): Role {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return Role.valueOf(claims.get(JwtConfig.ROLE_CLAIM, String::class.java))
    }
}