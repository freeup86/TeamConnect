package com.teamconnect.notificationservice.dto

import com.teamconnect.notificationservice.model.NotificationType
import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val title: String,
    val content: String,
    val type: NotificationType,
    val isRead: Boolean,
    val relatedEntityId: Long?,
    val createdAt: LocalDateTime
)

data class CreateNotificationRequest(
    val userId: Long,
    val title: String,
    val content: String,
    val type: NotificationType,
    val relatedEntityId: Long? = null
)

data class NotificationCountResponse(
    val total: Long,
    val unread: Long
)

data class PagedResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val lastPage: Boolean
)

data class WebSocketNotification(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val type: NotificationType,
    val relatedEntityId: Long?,
    val createdAt: LocalDateTime
)