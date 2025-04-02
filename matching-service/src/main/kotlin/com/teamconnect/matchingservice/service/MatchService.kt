package com.teamconnect.matchingservice.service

import com.teamconnect.common.exception.BadRequestException
import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import com.teamconnect.matchingservice.dto.MatchRequest
import com.teamconnect.matchingservice.dto.MatchResponse
import com.teamconnect.matchingservice.dto.UserSummary
import com.teamconnect.matchingservice.model.Match
import com.teamconnect.matchingservice.model.MatchStatus
import com.teamconnect.matchingservice.repository.MatchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val recommendationService: RecommendationService,
    private val userService: UserService
) {
    private val log = logger()

    @Transactional
    fun requestMatch(sourceUserId: Long, matchRequest: MatchRequest): MatchResponse {
        val targetUserId = matchRequest.targetUserId

        if (sourceUserId == targetUserId) {
            throw BadRequestException("Cannot match with yourself")
        }

        log.info("Processing match request from user $sourceUserId to user $targetUserId")

        // Check if match already exists
        val existingMatch = matchRepository.findBySourceUserIdAndTargetUserId(sourceUserId, targetUserId)
        if (existingMatch.isPresent) {
            log.info("Match already exists with status: ${existingMatch.get().status}")
            return mapToMatchResponse(existingMatch.get())
        }

        // Calculate match score
        val recommendations = recommendationService.getRecommendations(sourceUserId)
        val targetRecommendation = recommendations.find { it.user.id == targetUserId }
        val score = targetRecommendation?.score ?: 0.5 // Default score if not in recommendations

        // Create new match
        val match = Match(
            sourceUserId = sourceUserId,
            targetUserId = targetUserId,
            score = score,
            status = MatchStatus.PENDING,
            createdAt = LocalDateTime.now()
        )

        val savedMatch = matchRepository.save(match)
        log.info("Match created with id: ${savedMatch.id}")

        return mapToMatchResponse(savedMatch)
    }

    @Transactional
    fun acceptMatch(userId: Long, matchId: Long): MatchResponse {
        log.info("User $userId accepting match $matchId")

        val match = getMatchForUser(matchId, userId)

        if (match.status != MatchStatus.PENDING) {
            throw BadRequestException("Match is already ${match.status}")
        }

        match.status = MatchStatus.ACCEPTED
        val updatedMatch = matchRepository.save(match)

        // Create reciprocal match if it doesn't exist
        val reciprocalMatch = matchRepository.findBySourceUserIdAndTargetUserId(match.targetUserId, match.sourceUserId)
        if (reciprocalMatch.isEmpty) {
            val newReciprocalMatch = Match(
                sourceUserId = match.targetUserId,
                targetUserId = match.sourceUserId,
                score = match.score,
                status = MatchStatus.ACCEPTED,
                createdAt = LocalDateTime.now()
            )
            matchRepository.save(newReciprocalMatch)
            log.info("Created reciprocal match with id: ${newReciprocalMatch.id}")
        } else {
            val existingReciprocalMatch = reciprocalMatch.get()
            existingReciprocalMatch.status = MatchStatus.ACCEPTED
            matchRepository.save(existingReciprocalMatch)
            log.info("Updated reciprocal match with id: ${existingReciprocalMatch.id}")
        }

        return mapToMatchResponse(updatedMatch)
    }

    @Transactional
    fun rejectMatch(userId: Long, matchId: Long): MatchResponse {
        log.info("User $userId rejecting match $matchId")

        val match = getMatchForUser(matchId, userId)

        if (match.status != MatchStatus.PENDING) {
            throw BadRequestException("Match is already ${match.status}")
        }

        match.status = MatchStatus.REJECTED
        val updatedMatch = matchRepository.save(match)

        return mapToMatchResponse(updatedMatch)
    }

    @Transactional(readOnly = true)
    fun getOutgoingMatches(userId: Long): List<MatchResponse> {
        log.info("Getting outgoing matches for user $userId")

        return matchRepository.findBySourceUserIdAndStatus(userId, MatchStatus.PENDING)
            .map { mapToMatchResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getIncomingMatches(userId: Long): List<MatchResponse> {
        log.info("Getting incoming matches for user $userId")

        return matchRepository.findByTargetUserIdAndStatus(userId, MatchStatus.PENDING)
            .map { mapToMatchResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getAcceptedMatches(userId: Long): List<MatchResponse> {
        log.info("Getting accepted matches for user $userId")

        val outgoing = matchRepository.findBySourceUserIdAndStatus(userId, MatchStatus.ACCEPTED)
        val incoming = matchRepository.findByTargetUserIdAndStatus(userId, MatchStatus.ACCEPTED)

        return (outgoing + incoming).map { mapToMatchResponse(it) }
    }

    private fun getMatchForUser(matchId: Long, userId: Long): Match {
        val match = matchRepository.findById(matchId)
            .orElseThrow { ResourceNotFoundException("Match not found with id: $matchId") }

        if (match.targetUserId != userId) {
            throw BadRequestException("User $userId is not the target of match $matchId")
        }

        return match
    }

    private fun mapToMatchResponse(match: Match): MatchResponse {
        val sourceUserDetails = userService.getUserDetails(match.sourceUserId)
        val targetUserDetails = userService.getUserDetails(match.targetUserId)

        return MatchResponse(
            id = match.id,
            sourceUser = UserSummary(
                id = sourceUserDetails.id,
                name = sourceUserDetails.name,
                department = sourceUserDetails.department,
                skills = sourceUserDetails.skills
            ),
            targetUser = UserSummary(
                id = targetUserDetails.id,
                name = targetUserDetails.name,
                department = targetUserDetails.department,
                skills = targetUserDetails.skills
            ),
            score = match.score,
            status = match.status,
            createdAt = match.createdAt
        )
    }
}