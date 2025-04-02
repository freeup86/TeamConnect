package com.teamconnect.matchingservice.controller

import com.teamconnect.common.util.logger
import com.teamconnect.matchingservice.dto.MatchRequest
import com.teamconnect.matchingservice.dto.MatchResponse
import com.teamconnect.matchingservice.dto.RecommendationResponse
import com.teamconnect.matchingservice.service.MatchService
import com.teamconnect.matchingservice.service.RecommendationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class MatchController(
    private val matchService: MatchService,
    private val recommendationService: RecommendationService
) {
    private val log = logger()

    @GetMapping("/recommendations")
    fun getRecommendations(
        @RequestHeader("X-User-ID") userId: Long,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<List<RecommendationResponse>> {
        log.info("Fetching recommendations for user: $userId, limit: $limit")
        val recommendations = recommendationService.getRecommendations(userId, limit)
        return ResponseEntity.ok(recommendations)
    }

    @PostMapping("/request")
    fun requestMatch(
        @RequestHeader("X-User-ID") userId: Long,
        @Valid @RequestBody matchRequest: MatchRequest
    ): ResponseEntity<MatchResponse> {
        log.info("User $userId requesting match with user ${matchRequest.targetUserId}")
        val match = matchService.requestMatch(userId, matchRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(match)
    }

    @PutMapping("/{matchId}/accept")
    fun acceptMatch(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable matchId: Long
    ): ResponseEntity<MatchResponse> {
        log.info("User $userId accepting match $matchId")
        val match = matchService.acceptMatch(userId, matchId)
        return ResponseEntity.ok(match)
    }

    @PutMapping("/{matchId}/reject")
    fun rejectMatch(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable matchId: Long
    ): ResponseEntity<MatchResponse> {
        log.info("User $userId rejecting match $matchId")
        val match = matchService.rejectMatch(userId, matchId)
        return ResponseEntity.ok(match)
    }

    @GetMapping("/outgoing")
    fun getOutgoingMatches(@RequestHeader("X-User-ID") userId: Long): ResponseEntity<List<MatchResponse>> {
        log.info("Fetching outgoing matches for user: $userId")
        val matches = matchService.getOutgoingMatches(userId)
        return ResponseEntity.ok(matches)
    }

    @GetMapping("/incoming")
    fun getIncomingMatches(@RequestHeader("X-User-ID") userId: Long): ResponseEntity<List<MatchResponse>> {
        log.info("Fetching incoming matches for user: $userId")
        val matches = matchService.getIncomingMatches(userId)
        return ResponseEntity.ok(matches)
    }

    @GetMapping("/connections")
    fun getConnections(@RequestHeader("X-User-ID") userId: Long): ResponseEntity<List<MatchResponse>> {
        log.info("Fetching connections for user: $userId")
        val matches = matchService.getAcceptedMatches(userId)
        return ResponseEntity.ok(matches)
    }
}