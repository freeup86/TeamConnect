package com.teamconnect.userprofileservice.controller

import com.teamconnect.common.util.logger
import com.teamconnect.userprofileservice.dto.*
import com.teamconnect.userprofileservice.service.ProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
class ProfileController(
    private val profileService: ProfileService
) {
    private val log = logger()

    @PostMapping("/{userId}")
    fun createProfile(
        @PathVariable userId: Long,
        @Valid @RequestBody profileRequest: ProfileRequest
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to create profile for user: $userId")
        val profile = profileService.createProfile(userId, profileRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(profile)
    }

    @GetMapping("/{userId}")
    fun getProfile(@PathVariable userId: Long): ResponseEntity<ProfileResponse> {
        log.info("Received request to get profile for user: $userId")
        val profile = profileService.getProfile(userId)
        return ResponseEntity.ok(profile)
    }

    @PutMapping("/{userId}")
    fun updateProfile(
        @PathVariable userId: Long,
        @Valid @RequestBody profileRequest: ProfileRequest
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to update profile for user: $userId")
        val profile = profileService.updateProfile(userId, profileRequest)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/{userId}/skills")
    fun addSkill(
        @PathVariable userId: Long,
        @Valid @RequestBody skillRequest: SkillRequest
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to add skill for user: $userId")
        val profile = profileService.addSkill(userId, skillRequest.skill)
        return ResponseEntity.ok(profile)
    }

    @DeleteMapping("/{userId}/skills/{skill}")
    fun removeSkill(
        @PathVariable userId: Long,
        @PathVariable skill: String
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to remove skill for user: $userId")
        val profile = profileService.removeSkill(userId, skill)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/{userId}/interests")
    fun addInterest(
        @PathVariable userId: Long,
        @Valid @RequestBody interestRequest: InterestRequest
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to add interest for user: $userId")
        val profile = profileService.addInterest(userId, interestRequest.interest)
        return ResponseEntity.ok(profile)
    }

    @DeleteMapping("/{userId}/interests/{interest}")
    fun removeInterest(
        @PathVariable userId: Long,
        @PathVariable interest: String
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to remove interest for user: $userId")
        val profile = profileService.removeInterest(userId, interest)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/{userId}/learning-goals")
    fun addLearningGoal(
        @PathVariable userId: Long,
        @Valid @RequestBody learningGoalRequest: LearningGoalRequest
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to add learning goal for user: $userId")
        val profile = profileService.addLearningGoal(userId, learningGoalRequest.learningGoal)
        return ResponseEntity.ok(profile)
    }

    @DeleteMapping("/{userId}/learning-goals/{learningGoal}")
    fun removeLearningGoal(
        @PathVariable userId: Long,
        @PathVariable learningGoal: String
    ): ResponseEntity<ProfileResponse> {
        log.info("Received request to remove learning goal for user: $userId")
        val profile = profileService.removeLearningGoal(userId, learningGoal)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/search")
    fun searchProfiles(@RequestParam skill: String): ResponseEntity<List<ProfileResponse>> {
        log.info("Received request to search profiles by skill: $skill")
        val profiles = profileService.searchBySkill(skill)
        return ResponseEntity.ok(profiles)
    }

    @GetMapping("/department/{department}")
    fun getProfilesByDepartment(@PathVariable department: String): ResponseEntity<List<ProfileResponse>> {
        log.info("Received request to get profiles by department: $department")
        val profiles = profileService.getProfilesByDepartment(department)
        return ResponseEntity.ok(profiles)
    }
}