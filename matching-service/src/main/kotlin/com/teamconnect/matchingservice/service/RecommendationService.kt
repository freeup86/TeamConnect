package com.teamconnect.matchingservice.service

import com.teamconnect.common.util.logger
import com.teamconnect.matchingservice.dto.RecommendationResponse
import com.teamconnect.matchingservice.dto.UserSummary
import com.teamconnect.matchingservice.repository.UserProfileRepository
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val userProfileRepository: UserProfileRepository,
    private val userService: UserService
) {
    private val log = logger()

    fun getRecommendations(userId: Long, limit: Int = 10): List<RecommendationResponse> {
        log.info("Generating recommendations for user: $userId")

        // Get the user's details
        val userDetails = userService.getUserDetails(userId)

        // Get all other profiles from cache
        val allProfiles = userProfileRepository.findAll()
            .filter { it.userId != userId }

        if (allProfiles.isEmpty()) {
            log.warn("No other profiles found for recommendation")
            return emptyList()
        }

        // Calculate recommendations
        val recommendations = allProfiles.map { profile ->
            // Calculate similarity score
            val skillIntersection = userDetails.skills.intersect(profile.skills)
            val interestIntersection = userDetails.interests.intersect(profile.interests)

            val skillScore = calculateSetSimilarity(userDetails.skills, profile.skills) * 0.5
            val interestScore = calculateSetSimilarity(userDetails.interests, profile.interests) * 0.3

            // Boost score for cross-departmental matches
            val departmentScore = if (userDetails.department != profile.department) 0.2 else 0.0

            // Career stage alignment
            val careerStageScore = if (userDetails.careerStage == profile.careerStage) 0.0 else 0.1

            val totalScore = skillScore + interestScore + departmentScore + careerStageScore

            RecommendationResponse(
                user = UserSummary(
                    id = profile.userId,
                    name = profile.name,
                    department = profile.department,
                    skills = profile.skills
                ),
                score = totalScore,
                matchingSkills = skillIntersection,
                matchingInterests = interestIntersection
            )
        }

        // Sort by score and return top recommendations
        return recommendations
            .sortedByDescending { it.score }
            .take(limit)
    }

    private fun calculateSetSimilarity(set1: Set<String>, set2: Set<String>): Double {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0

        val intersection = set1.intersect(set2).size
        val union = set1.size + set2.size - intersection

        return intersection.toDouble() / union.toDouble()
    }
}