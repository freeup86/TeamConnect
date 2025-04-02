package com.teamconnect.notificationservice.model

import com.teamconnect.common.model.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: NotificationType,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = true)
    val relatedEntityId: Long? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity()

enum class NotificationType {
    MATCH_REQUEST,
    MATCH_ACCEPTED,
    MATCH_REJECTED,
    ACTIVITY_INVITATION,
    ACTIVITY_REMINDER,
    ACTIVITY_CANCELLED,
    ACTIVITY_UPDATED,
    NEW_PARTICIPANT,
    PARTICIPANT_LEFT,
    SKILL_RECOMMENDATION,
    SYSTEM_ANNOUNCEMENT
}