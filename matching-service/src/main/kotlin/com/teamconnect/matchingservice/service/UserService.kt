package com.teamconnect.matchingservice.service

import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import com.teamconnect.matchingservice.client.AuthServiceClient
import com.teamconnect.matchingservice.client.UserProfileClient
import com.teamconnect.matchingservice.model.UserProfile
import com.teamconnect.matchingservice.repository.UserProfileRepository
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userProfileRepository: UserProfileRepository,
    private val userProfileClient: UserProfileClient,
    private val authServiceClient: AuthServiceClient
) {
    private val log = logger()

    @CircuitBreaker(name = "userProfileService", fallbackMethod = "getUserDetailsFromCache")
    fun getUserDetails(userId: Long): UserDetailsDto {
        log.info("Fetching user details for user: $userId")

        // Get basic user info from Auth Service
        val userBasicInfo = authServiceClient.getUserBasicInfo(userId)

        // Get user profile from Profile Service
        val userProfile = userProfileClient.getProfile(userId)

        // Cache the profile
        cacheUserProfile(
            UserProfile(
                userId = userId,
                name = userBasicInfo.name,
                department = userProfile.department,
                skills = userProfile.skills,
                interests = userProfile.interests,
                careerStage = userProfile.careerStage
            )
        )

        return UserDetailsDto(
            id = userId,
            name = userBasicInfo.name,
            department = userProfile.department,
            skills = userProfile.skills,
            interests = userProfile.interests,
            careerStage = userProfile.careerStage
        )
    }

    private fun getUserDetailsFromCache(userId: Long, exception: Throwable): UserDetailsDto {
        log.warn("Fallback: Using cached user details for user: $userId, error: ${exception.message}")

        val cachedProfile = userProfileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User profile not found for user: $userId") }

        return UserDetailsDto(
            id = cachedProfile.userId,
            name = cachedProfile.name,
            department = cachedProfile.department,
            skills = cachedProfile.skills,
            interests = cachedProfile.interests,
            careerStage = cachedProfile.careerStage
        )
    }

    private fun cacheUserProfile(userProfile: UserProfile) {
        userProfileRepository.save(userProfile)
    }
}

data class UserDetailsDto(
    val id: Long,
    val name: String,
    val department: String,
    val skills: Set<String>,
    val interests: Set<String>,
    val careerStage: String
)