package com.teamconnect.common.security

data class JwtConfig(
    val secret: String,
    val expirationMs: Long,
    val tokenPrefix: String,
    val headerName: String
) {
    companion object {
        const val ROLE_CLAIM = "role"
        const val USER_ID_CLAIM = "userId"
    }
}