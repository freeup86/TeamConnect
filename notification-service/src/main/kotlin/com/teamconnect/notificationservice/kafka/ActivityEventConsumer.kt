package com.teamconnect.notificationservice.kafka

import com.teamconnect.common.util.logger
import com.teamconnect.notificationservice.dto.CreateNotificationRequest
import com.teamconnect.notificationservice.model.NotificationType
import com.teamconnect.notificationservice.service.NotificationService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ActivityEventConsumer(
    private val notificationService: NotificationService
) {
    private val log = logger()

    @KafkaListener(topics = ["activity-events"], groupId = "notification-service")
    fun consume(event: ActivityEvent) {
        log.info("Consuming activity event: type=${event.eventType}, activityId=${event.activityId}")

        when (event.eventType) {
            ActivityEventType.CREATED -> handleActivityCreated(event)
            ActivityEventType.UPDATED -> handleActivityUpdated(event)
            ActivityEventType.CANCELLED -> handleActivityCancelled(event)
            ActivityEventType.PARTICIPANT_JOINED -> handleParticipantJoined(event)
            ActivityEventType.PARTICIPANT_LEFT -> handleParticipantLeft(event)
            ActivityEventType.STARTED -> handleActivityStarted(event)
            ActivityEventType.COMPLETED -> handleActivityCompleted(event)
        }
    }

    private fun handleActivityCreated(event: ActivityEvent) {
        // Notify creator
        notificationService.createNotification(
            CreateNotificationRequest(
                userId = event.createdBy,
                title = "Activity Created",
                content = "Your activity '${event.title}' has been created successfully.",
                type = NotificationType.SYSTEM_ANNOUNCEMENT,
                relatedEntityId = event.activityId
            )
        )
    }

    private fun handleActivityUpdated(event: ActivityEvent) {
        // Notify all participants except the creator
        val participantsToNotify = event.participantIds.filter { it != event.createdBy }

        participantsToNotify.forEach { participantId ->
            notificationService.createNotification(
                CreateNotificationRequest(
                    userId = participantId,
                    title = "Activity Updated",
                    content = "The activity '${event.title}' has been updated.",
                    type = NotificationType.ACTIVITY_UPDATED,
                    relatedEntityId = event.activityId
                )
            )
        }
    }

    private fun handleActivityCancelled(event: ActivityEvent) {
        // Notify all participants except the creator
        val participantsToNotify = event.participantIds.filter { it != event.createdBy }

        participantsToNotify.forEach { participantId ->
            notificationService.createNotification(
                CreateNotificationRequest(
                    userId = participantId,
                    title = "Activity Cancelled",
                    content = "The activity '${event.title}' has been cancelled.",
                    type = NotificationType.ACTIVITY_CANCELLED,
                    relatedEntityId = event.activityId
                )
            )
        }
    }

    private fun handleParticipantJoined(event: ActivityEvent) {
        // Notify the creator
        notificationService.createNotification(
            CreateNotificationRequest(
                userId = event.createdBy,
                title = "New Participant",
                content = "A new participant has joined your activity '${event.title}'.",
                type = NotificationType.NEW_PARTICIPANT,
                relatedEntityId = event.activityId
            )
        )
    }

    private fun handleParticipantLeft(event: ActivityEvent) {
        // Notify the creator
        notificationService.createNotification(
            CreateNotificationRequest(
                userId = event.createdBy,
                title = "Participant Left",
                content = "A participant has left your activity '${event.title}'.",
                type = NotificationType.PARTICIPANT_LEFT,
                relatedEntityId = event.activityId
            )
        )
    }

    private fun handleActivityStarted(event: ActivityEvent) {
        // Notify all participants
        event.participantIds.forEach { participantId ->
            notificationService.createNotification(
                CreateNotificationRequest(
                    userId = participantId,
                    title = "Activity Started",
                    content = "The activity '${event.title}' has started.",
                    type = NotificationType.ACTIVITY_REMINDER,
                    relatedEntityId = event.activityId
                )
            )
        }
    }

    private fun handleActivityCompleted(event: ActivityEvent) {
        // Notify all participants
        event.participantIds.forEach { participantId ->
            notificationService.createNotification(
                CreateNotificationRequest(
                    userId = participantId,
                    title = "Activity Completed",
                    content = "The activity '${event.title}' has been completed.",
                    type = NotificationType.SYSTEM_ANNOUNCEMENT,
                    relatedEntityId = event.activityId
                )
            )
        }
    }
}

data class ActivityEvent(
    val eventType: ActivityEventType,
    val activityId: Long,
    val title: String,
    val description: String,
    val createdBy: Long,
    val scheduledAt: String,
    val status: String,
    val participantIds: Set<Long>
)

enum class ActivityEventType {
    CREATED, UPDATED, CANCELLED, PARTICIPANT_JOINED, PARTICIPANT_LEFT, STARTED, COMPLETED
}