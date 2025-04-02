package com.teamconnect.userprofileservice.dto

import jakarta.validation.constraints.NotBlank

data class ProfileRequest(
    @field:NotBlank(message = "Department is required")
    val department: String,

    val skills: Set<String> = emptySet(),

    val interests: Set<String> = emptySet(),

    @field:NotBlank(message = "Career stage is required")
    val careerStage: String,

    val learningGoals: Set<String> = emptySet(),

    val privacySettings: PrivacySettingsDto = PrivacySettingsDto()
)

data class PrivacySettingsDto(
    val isProfilePublic: Boolean = true,
    val isSkillsPublic: Boolean = true,
    val isInterestsPublic: Boolean = true
)

data class ProfileResponse(
    val userId: Long,
    val department: String,
    val skills: Set<String>,
    val interests: Set<String>,
    val careerStage: String,
    val learningGoals: Set<String>,
    val privacySettings: PrivacySettingsDto
)

data class SkillRequest(
    @field:NotBlank(message = "Skill is required")
    val skill: String
)

data class InterestRequest(
    @field:NotBlank(message = "Interest is required")
    val interest: String
)

data class LearningGoalRequest(
    @field:NotBlank(message = "Learning goal is required")
    val learningGoal: String
)