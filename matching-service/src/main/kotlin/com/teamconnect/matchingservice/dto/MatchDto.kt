package com.teamconnect.matchingservice.dto

import com.teamconnect.matchingservice.model.MatchStatus
import java.time.LocalDateTime
import jakarta.validation.constraints.NotNull

data class MatchRequest(
    @field:NotNull(message = "Target user ID is required")
    val targetUserId: Long
)

data class MatchResponse(
    val id: Long,
    val sourceUser: UserSummary,
    val targetUser: UserSummary,
    val score: Double,
    val status: MatchStatus,
    val createdAt: LocalDateTime
)

data class UserSummary(
    val id: Long,
    val name: String,
    val department: String,
    val skills: Set<String>
)

data class RecommendationResponse(
    val user: UserSummary,
    val score: Double,
    val matchingSkills: Set<String>,
    val matchingInterests: Set<String>
)