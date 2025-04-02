package com.teamconnect.userprofileservice.service

import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import com.teamconnect.userprofileservice.dto.PrivacySettingsDto
import com.teamconnect.userprofileservice.dto.ProfileRequest
import com.teamconnect.userprofileservice.dto.ProfileResponse
import com.teamconnect.userprofileservice.model.PrivacySettings
import com.teamconnect.userprofileservice.model.Profile
import com.teamconnect.userprofileservice.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileService(
    private val profileRepository: ProfileRepository
) {
    private val log = logger()

    @Transactional
    fun createProfile(userId: Long, profileRequest: ProfileRequest): ProfileResponse {
        log.info("Creating profile for user: $userId")

        val profile = Profile(
            userId = userId,
            department = profileRequest.department,
            skills = profileRequest.skills.toMutableSet(),
            interests = profileRequest.interests.toMutableSet(),
            careerStage = profileRequest.careerStage,
            learningGoals = profileRequest.learningGoals.toMutableSet(),
            privacySettings = PrivacySettings(
                isProfilePublic = profileRequest.privacySettings.isProfilePublic,
                isSkillsPublic = profileRequest.privacySettings.isSkillsPublic,
                isInterestsPublic = profileRequest.privacySettings.isInterestsPublic
            )
        )

        val savedProfile = profileRepository.save(profile)
        return mapToProfileResponse(savedProfile)
    }

    @Transactional(readOnly = true)
    fun getProfile(userId: Long): ProfileResponse {
        log.info("Fetching profile for user: $userId")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        return mapToProfileResponse(profile)
    }

    @Transactional
    fun updateProfile(userId: Long, profileRequest: ProfileRequest): ProfileResponse {
        log.info("Updating profile for user: $userId")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.department = profileRequest.department
        profile.careerStage = profileRequest.careerStage
        profile.skills = profileRequest.skills.toMutableSet()
        profile.interests = profileRequest.interests.toMutableSet()
        profile.learningGoals = profileRequest.learningGoals.toMutableSet()
        profile.privacySettings = PrivacySettings(
            isProfilePublic = profileRequest.privacySettings.isProfilePublic,
            isSkillsPublic = profileRequest.privacySettings.isSkillsPublic,
            isInterestsPublic = profileRequest.privacySettings.isInterestsPublic
        )

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun addSkill(userId: Long, skill: String): ProfileResponse {
        log.info("Adding skill for user: $userId, skill: $skill")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.skills.add(skill)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun removeSkill(userId: Long, skill: String): ProfileResponse {
        log.info("Removing skill for user: $userId, skill: $skill")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.skills.remove(skill)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun addInterest(userId: Long, interest: String): ProfileResponse {
        log.info("Adding interest for user: $userId, interest: $interest")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.interests.add(interest)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun removeInterest(userId: Long, interest: String): ProfileResponse {
        log.info("Removing interest for user: $userId, interest: $interest")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.interests.remove(interest)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun addLearningGoal(userId: Long, learningGoal: String): ProfileResponse {
        log.info("Adding learning goal for user: $userId, goal: $learningGoal")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.learningGoals.add(learningGoal)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional
    fun removeLearningGoal(userId: Long, learningGoal: String): ProfileResponse {
        log.info("Removing learning goal for user: $userId, goal: $learningGoal")

        val profile = profileRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("Profile not found for user id: $userId") }

        profile.learningGoals.remove(learningGoal)

        val updatedProfile = profileRepository.save(profile)
        return mapToProfileResponse(updatedProfile)
    }

    @Transactional(readOnly = true)
    fun searchBySkill(skill: String): List<ProfileResponse> {
        log.info("Searching profiles by skill: $skill")

        return profileRepository.findBySkillsContaining(skill)
            .map { mapToProfileResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getProfilesByDepartment(department: String): List<ProfileResponse> {
        log.info("Fetching profiles by department: $department")

        return profileRepository.findByDepartment(department)
            .map { mapToProfileResponse(it) }
    }

    private fun mapToProfileResponse(profile: Profile): ProfileResponse {
        return ProfileResponse(
            userId = profile.userId,
            department = profile.department,
            skills = profile.skills,
            interests = profile.interests,
            careerStage = profile.careerStage,
            learningGoals = profile.learningGoals,
            privacySettings = PrivacySettingsDto(
                isProfilePublic = profile.privacySettings.isProfilePublic,
                isSkillsPublic = profile.privacySettings.isSkillsPublic,
                isInterestsPublic = profile.privacySettings.isInterestsPublic
            )
        )
    }
}