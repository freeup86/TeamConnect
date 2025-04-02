package com.teamconnect.activityservice.controller

import com.teamconnect.activityservice.dto.*
import com.teamconnect.activityservice.service.ActivityService
import com.teamconnect.common.util.logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class ActivityController(
    private val activityService: ActivityService
) {
    private val log = logger()

    @PostMapping
    fun createActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @Valid @RequestBody request: ActivityRequest
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request to create activity from user: $userId")
        val activity = activityService.createActivity(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(activity)
    }

    @GetMapping("/{id}")
    fun getActivity(@PathVariable id: Long): ResponseEntity<ActivityResponse> {
        log.info("Received request to get activity: $id")
        val activity = activityService.getActivity(id)
        return ResponseEntity.ok(activity)
    }

    @PutMapping("/{id}")
    fun updateActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: ActivityUpdateRequest
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request to update activity: $id from user: $userId")
        val activity = activityService.updateActivity(userId, id, request)
        return ResponseEntity.ok(activity)
    }

    @PutMapping("/{id}/cancel")
    fun cancelActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request to cancel activity: $id from user: $userId")
        val activity = activityService.cancelActivity(userId, id)
        return ResponseEntity.ok(activity)
    }

    @PostMapping("/{id}/join")
    fun joinActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request from user: $userId to join activity: $id")
        val activity = activityService.joinActivity(userId, id)
        return ResponseEntity.ok(activity)
    }

    @PostMapping("/{id}/leave")
    fun leaveActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request from user: $userId to leave activity: $id")
        val activity = activityService.leaveActivity(userId, id)
        return ResponseEntity.ok(activity)
    }

    @PutMapping("/{id}/start")
    fun startActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request to start activity: $id from user: $userId")
        val activity = activityService.startActivity(userId, id)
        return ResponseEntity.ok(activity)
    }

    @PutMapping("/{id}/complete")
    fun completeActivity(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        log.info("Received request to complete activity: $id from user: $userId")
        val activity = activityService.completeActivity(userId, id)
        return ResponseEntity.ok(activity)
    }

    @GetMapping("/upcoming")
    fun getUpcomingActivities(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<ActivityResponse>> {
        log.info("Received request to get upcoming activities")
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").ascending())
        val activities = activityService.getUpcomingActivities(pageable)
        return ResponseEntity.ok(activities)
    }

    @GetMapping("/created")
    fun getUserActivities(
        @RequestHeader("X-User-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<ActivityResponse>> {
        log.info("Received request to get activities created by user: $userId")
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").descending())
        val activities = activityService.getUserActivities(userId, pageable)
        return ResponseEntity.ok(activities)
    }

    @GetMapping("/participating")
    fun getParticipatingActivities(
        @RequestHeader("X-User-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<ActivityResponse>> {
        log.info("Received request to get activities where user $userId is participating")
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").ascending())
        val activities = activityService.getParticipatingActivities(userId, pageable)
        return ResponseEntity.ok(activities)
    }

    @GetMapping("/search")
    fun searchActivitiesBySkill(
        @RequestParam skill: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<ActivityResponse>> {
        log.info("Received request to search activities by skill: $skill")
        val pageable = PageRequest.of(page, size, Sort.by("scheduledAt").ascending())
        val activities = activityService.searchActivitiesBySkill(skill, pageable)
        return ResponseEntity.ok(activities)
    }
}