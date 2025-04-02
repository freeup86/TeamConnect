package com.teamconnect.activityservice.service

import com.teamconnect.activityservice.client.ProfileServiceClient
import com.teamconnect.activityservice.client.UserServiceClient
import com.teamconnect.activityservice.dto.*
import com.teamconnect.activityservice.kafka.ActivityEventProducer
import com.teamconnect.activityservice.model.Activity
import com.teamconnect.activityservice.model.ActivityStatus
import com.teamconnect.activityservice.model.Participant
import com.teamconnect.activityservice.repository.ActivityRepository
import com.teamconnect.activityservice.repository.ParticipantRepository
import com.teamconnect.common.exception.BadRequestException
import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val participantRepository: ParticipantRepository,
    private val userServiceClient: UserServiceClient,
    private val profileServiceClient: ProfileServiceClient,
    private val activityEventProducer: ActivityEventProducer
) {
    private val log = logger()

    @Transactional
    fun createActivity(userId: Long, request: ActivityRequest): ActivityResponse {
        log.info("Creating activity by user: $userId, title: ${request.title}")

        val userInfo = userServiceClient.getUserBasicInfo(userId)
        val userProfile = profileServiceClient.getProfile(userId)

        val participant = getOrCreateParticipant(userId, userInfo.name, userProfile.department)

        val activity = Activity(
            title = request.title,
            description = request.description,
            requiredSkills = request.requiredSkills.toMutableSet(),
            maxParticipants = request.maxParticipants,
            duration = request.duration,
            learningOutcomes = request.learningOutcomes.toMutableSet(),
            createdBy = userId,
            scheduledAt = request.scheduledAt,
            status = ActivityStatus.SCHEDULED,
            participants = mutableSetOf(participant)
        )

        val savedActivity = activityRepository.save(activity)

        // Publish event
        activityEventProducer.publishActivityCreated(savedActivity)

        return mapToActivityResponse(savedActivity)
    }

    @Transactional(readOnly = true)
    fun getActivity(activityId: Long): ActivityResponse {
        log.info("Fetching activity: $activityId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        return mapToActivityResponse(activity)
    }

    @Transactional
    fun updateActivity(userId: Long, activityId: Long, request: ActivityUpdateRequest): ActivityResponse {
        log.info("Updating activity: $activityId by user: $userId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.createdBy != userId) {
            throw BadRequestException("Only the creator can update the activity")
        }

        // Update the fields if they are provided
        request.title?.let { activity.title = it }
        request.description?.let { activity.description = it }
        request.requiredSkills?.let { activity.requiredSkills = it.toMutableSet() }
        request.maxParticipants?.let {
            if (it < activity.participants.size) {
                throw BadRequestException("Max participants cannot be less than current participants")
            }
            activity.maxParticipants = it
        }
        request.duration?.let { activity.duration = it }
        request.learningOutcomes?.let { activity.learningOutcomes = it.toMutableSet() }
        request.scheduledAt?.let { activity.scheduledAt = it }
        request.status?.let { activity.status = it }

        val updatedActivity = activityRepository.save(activity)

        // Publish event
        activityEventProducer.publishActivityUpdated(updatedActivity)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional
    fun cancelActivity(userId: Long, activityId: Long): ActivityResponse {
        log.info("Cancelling activity: $activityId by user: $userId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.createdBy != userId) {
            throw BadRequestException("Only the creator can cancel the activity")
        }

        activity.status = ActivityStatus.CANCELLED
        val updatedActivity = activityRepository.save(activity)

        // Publish event
        activityEventProducer.publishActivityCancelled(updatedActivity)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional
    fun joinActivity(userId: Long, activityId: Long): ActivityResponse {
        log.info("User $userId joining activity: $activityId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.status != ActivityStatus.SCHEDULED) {
            throw BadRequestException("Cannot join activity with status: ${activity.status}")
        }

        if (activity.participants.size >= activity.maxParticipants) {
            throw BadRequestException("Activity has reached maximum participants")
        }

        val userInfo = userServiceClient.getUserBasicInfo(userId)
        val userProfile = profileServiceClient.getProfile(userId)

        val participant = getOrCreateParticipant(userId, userInfo.name, userProfile.department)

        if (activity.participants.any { it.userId == userId }) {
            throw BadRequestException("User is already a participant")
        }

        activity.participants.add(participant)
        val updatedActivity = activityRepository.save(activity)

        // Publish event
        activityEventProducer.publishParticipantJoined(updatedActivity, userId)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional
    fun leaveActivity(userId: Long, activityId: Long): ActivityResponse {
        log.info("User $userId leaving activity: $activityId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.status != ActivityStatus.SCHEDULED) {
            throw BadRequestException("Cannot leave activity with status: ${activity.status}")
        }

        if (activity.createdBy == userId) {
            throw BadRequestException("Creator cannot leave the activity, cancel it instead")
        }

        val participant = activity.participants.find { it.userId == userId }
            ?: throw BadRequestException("User is not a participant")

        activity.participants.remove(participant)
        val updatedActivity = activityRepository.save(activity)

        // Publish event
        activityEventProducer.publishParticipantLeft(updatedActivity, userId)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional
    fun startActivity(userId: Long, activityId: Long): ActivityResponse {
        log.info("Starting activity: $activityId by user: $userId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.createdBy != userId) {
            throw BadRequestException("Only the creator can start the activity")
        }

        if (activity.status != ActivityStatus.SCHEDULED) {
            throw BadRequestException("Cannot start activity with status: ${activity.status}")
        }

        activity.status = ActivityStatus.IN_PROGRESS
        val updatedActivity = activityRepository.save(activity)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional
    fun completeActivity(userId: Long, activityId: Long): ActivityResponse {
        log.info("Completing activity: $activityId by user: $userId")

        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResourceNotFoundException("Activity not found with id: $activityId") }

        if (activity.createdBy != userId) {
            throw BadRequestException("Only the creator can complete the activity")
        }

        if (activity.status != ActivityStatus.IN_PROGRESS) {
            throw BadRequestException("Cannot complete activity with status: ${activity.status}")
        }

        activity.status = ActivityStatus.COMPLETED
        val updatedActivity = activityRepository.save(activity)

        return mapToActivityResponse(updatedActivity)
    }

    @Transactional(readOnly = true)
    fun getUpcomingActivities(pageable: Pageable): PagedResponse<ActivityResponse> {
        log.info("Fetching upcoming activities")

        val now = LocalDateTime.now()
        val activities = activityRepository.findByScheduledAtBetweenAndStatus(
            now, now.plusDays(30), ActivityStatus.SCHEDULED, pageable
        )

        return mapToPagedResponse(activities)
    }

    @Transactional(readOnly = true)
    fun getUserActivities(userId: Long, pageable: Pageable): PagedResponse<ActivityResponse> {
        log.info("Fetching activities created by user: $userId")

        val activities = activityRepository.findByCreatedBy(userId, pageable)

        return mapToPagedResponse(activities)
    }

    @Transactional(readOnly = true)
    fun getParticipatingActivities(userId: Long, pageable: Pageable): PagedResponse<ActivityResponse> {
        log.info("Fetching activities where user $userId is a participant")

        val activities = activityRepository.findByParticipantId(userId, pageable)

        return mapToPagedResponse(activities)
    }

    @Transactional(readOnly = true)
    fun searchActivitiesBySkill(skill: String, pageable: Pageable): PagedResponse<ActivityResponse> {
        log.info("Searching activities by skill: $skill")

        val activities = activityRepository.findByRequiredSkillsContaining(skill, pageable)

        return mapToPagedResponse(activities)
    }

    private fun getOrCreateParticipant(userId: Long, name: String, department: String): Participant {
        return participantRepository.findByUserId(userId).orElseGet {
            participantRepository.save(
                Participant(
                    userId = userId,
                    name = name,
                    department = department
                )
            )
        }
    }

    private fun mapToActivityResponse(activity: Activity): ActivityResponse {
        val creatorInfo = userServiceClient.getUserBasicInfo(activity.createdBy)
        val creatorProfile = profileServiceClient.getProfile(activity.createdBy)

        return ActivityResponse(
            id = activity.id,
            title = activity.title,
            description = activity.description,
            requiredSkills = activity.requiredSkills,
            maxParticipants = activity.maxParticipants,
            currentParticipants = activity.participants.size,
            duration = activity.duration,
            learningOutcomes = activity.learningOutcomes,
            creator = UserSummaryDto(
                id = creatorInfo.id,
                name = creatorInfo.name,
                department = creatorProfile.department
            ),
            scheduledAt = activity.scheduledAt,
            status = activity.status,
            participants = activity.participants.map {
                UserSummaryDto(
                    id = it.userId,
                    name = it.name,
                    department = it.department
                )
            }
        )
    }

    private fun mapToPagedResponse(page: Page<Activity>): PagedResponse<ActivityResponse> {
        val activities = page.content.map { mapToActivityResponse(it) }

        return PagedResponse(
            content = activities,
            pageNumber = page.number,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            lastPage = page.isLast
        )
    }
}