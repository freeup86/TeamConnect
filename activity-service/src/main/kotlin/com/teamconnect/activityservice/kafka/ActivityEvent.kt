package com.teamconnect.activityservice.kafka

import com.teamconnect.activityservice.model.ActivityStatus
import java.time.LocalDateTime

data class ActivityEvent(
    val eventType: ActivityEventType,
    val activityId: Long,
    val title: String,
    val description: String,
    val createdBy: Long,
    val scheduledAt: LocalDateTime,
    val status: ActivityStatus,
    val participantIds: Set<Long>
)

enum class ActivityEventType {
    CREATED, UPDATED, CANCELLED, PARTICIPANT_JOINED, PARTICIPANT_LEFT, STARTED, COMPLETED
}