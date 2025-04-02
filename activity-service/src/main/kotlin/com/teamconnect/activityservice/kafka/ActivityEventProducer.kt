package com.teamconnect.activityservice.kafka

import com.teamconnect.activityservice.model.Activity
import com.teamconnect.common.util.logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ActivityEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, ActivityEvent>
) {
    private val log = logger()

    private val TOPIC = "activity-events"

    fun publishActivityCreated(activity: Activity) {
        publish(
            ActivityEvent(
                eventType = ActivityEventType.CREATED,
                activityId = activity.id,
                title = activity.title,
                description = activity.description,
                createdBy = activity.createdBy,
                scheduledAt = activity.scheduledAt,
                status = activity.status,
                participantIds = activity.participants.map { it.userId }.toSet()
            )
        )
    }

    fun publishActivityUpdated(activity: Activity) {
        publish(
            ActivityEvent(
                eventType = ActivityEventType.UPDATED,
                activityId = activity.id,
                title = activity.title,
                description = activity.description,
                createdBy = activity.createdBy,
                scheduledAt = activity.scheduledAt,
                status = activity.status,
                participantIds = activity.participants.map { it.userId }.toSet()
            )
        )
    }

    fun publishActivityCancelled(activity: Activity) {
        publish(
            ActivityEvent(
                eventType = ActivityEventType.CANCELLED,
                activityId = activity.id,
                title = activity.title,
                description = activity.description,
                createdBy = activity.createdBy,
                scheduledAt = activity.scheduledAt,
                status = activity.status,
                participantIds = activity.participants.map { it.userId }.toSet()
            )
        )
    }

    fun publishParticipantJoined(activity: Activity, participantId: Long) {
        publish(
            ActivityEvent(
                eventType = ActivityEventType.PARTICIPANT_JOINED,
                activityId = activity.id,
                title = activity.title,
                description = activity.description,
                createdBy = activity.createdBy,
                scheduledAt = activity.scheduledAt,
                status = activity.status,
                participantIds = activity.participants.map { it.userId }.toSet()
            )
        )
    }

    fun publishParticipantLeft(activity: Activity, participantId: Long) {
        publish(
            ActivityEvent(
                eventType = ActivityEventType.PARTICIPANT_LEFT,
                activityId = activity.id,
                title = activity.title,
                description = activity.description,
                createdBy = activity.createdBy,
                scheduledAt = activity.scheduledAt,
                status = activity.status,
                participantIds = activity.participants.map { it.userId }.toSet()
            )
        )
    }

    private fun publish(event: ActivityEvent) {
        log.info("Publishing activity event: type=${event.eventType}, activityId=${event.activityId}")
        kafkaTemplate.send(TOPIC, event.activityId.toString(), event)
    }
}