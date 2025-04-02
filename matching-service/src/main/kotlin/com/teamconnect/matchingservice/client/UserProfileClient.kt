package com.teamconnect.matchingservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user-profile-service", path = "/api/profiles")
interface UserProfileClient {

    @GetMapping("/{userId}")
    fun getProfile(@PathVariable userId: Long): ProfileDto
}

data class ProfileDto(
    val userId: Long,
    val department: String,
    val skills: Set<String>,
    val interests: Set<String>,
    val careerStage: String,
    val learningGoals: Set<String>,
    val privacySettings: PrivacySettingsDto
)

data class PrivacySettingsDto(
    val isProfilePublic: Boolean,
    val isSkillsPublic: Boolean,
    val isInterestsPublic: Boolean
)