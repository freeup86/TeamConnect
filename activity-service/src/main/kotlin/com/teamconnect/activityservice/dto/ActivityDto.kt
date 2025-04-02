package com.teamconnect.activityservice.dto

import com.teamconnect.activityservice.model.ActivityStatus
import java.time.LocalDateTime
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ActivityRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Description is required")
    val description: String,

    val requiredSkills: Set<String> = emptySet(),

    @field:Min(value = 2, message = "At least 2 participants are required")
    val maxParticipants: Int,

    @field:Min(value = 15, message = "Minimum duration is 15 minutes")
    val duration: Int, // Duration in minutes

    val learningOutcomes: Set<String> = emptySet(),

    @field:NotNull(message = "Scheduled date and time is required")
    @field:Future(message = "Scheduled date must be in the future")
    val scheduledAt: LocalDateTime
)

data class ActivityResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requiredSkills: Set<String>,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val duration: Int,
    val learningOutcomes: Set<String>,
    val creator: UserSummaryDto,
    val scheduledAt: LocalDateTime,
    val status: ActivityStatus,
    val participants: List<UserSummaryDto>
)

data class ActivityUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val requiredSkills: Set<String>? = null,
    val maxParticipants: Int? = null,
    val duration: Int? = null,
    val learningOutcomes: Set<String>? = null,
    val scheduledAt: LocalDateTime? = null,
    val status: ActivityStatus? = null
)

data class UserSummaryDto(
    val id: Long,
    val name: String,
    val department: String
)

data class PagedResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val lastPage: Boolean
)