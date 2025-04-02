package com.teamconnect.notificationservice.service

import com.teamconnect.common.exception.ResourceNotFoundException
import com.teamconnect.common.util.logger
import com.teamconnect.notificationservice.dto.*
import com.teamconnect.notificationservice.model.Notification
import com.teamconnect.notificationservice.model.NotificationType
import com.teamconnect.notificationservice.repository.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val log = logger()

    @Transactional
    fun createNotification(request: CreateNotificationRequest): NotificationResponse {
        log.info("Creating notification for user: ${request.userId}, type: ${request.type}")

        val notification = Notification(
            userId = request.userId,
            title = request.title,
            content = request.content,
            type = request.type,
            relatedEntityId = request.relatedEntityId,
            createdAt = LocalDateTime.now()
        )

        val savedNotification = notificationRepository.save(notification)

        // Send real-time notification via WebSocket
        sendWebSocketNotification(savedNotification)

        return mapToNotificationResponse(savedNotification)
    }

    @Transactional(readOnly = true)
    fun getNotificationById(id: Long): NotificationResponse {
        log.info("Fetching notification with id: $id")

        val notification = notificationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }

        return mapToNotificationResponse(notification)
    }

    @Transactional(readOnly = true)
    fun getUserNotifications(userId: Long, pageable: Pageable): PagedResponse<NotificationResponse> {
        log.info("Fetching notifications for user: $userId")

        val notifications = notificationRepository.findByUserId(userId, pageable)

        return mapToPagedResponse(notifications)
    }

    @Transactional(readOnly = true)
    fun getUnreadNotifications(userId: Long, pageable: Pageable): PagedResponse<NotificationResponse> {
        log.info("Fetching unread notifications for user: $userId")

        val notifications = notificationRepository.findByUserIdAndIsRead(userId, false, pageable)

        return mapToPagedResponse(notifications)
    }

    @Transactional(readOnly = true)
    fun getNotificationsByType(userId: Long, type: NotificationType, pageable: Pageable): PagedResponse<NotificationResponse> {
        log.info("Fetching notifications for user: $userId, type: $type")

        val notifications = notificationRepository.findByUserIdAndType(userId, type, pageable)

        return mapToPagedResponse(notifications)
    }

    @Transactional
    fun markAsRead(id: Long): NotificationResponse {
        log.info("Marking notification as read: $id")

        val notification = notificationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $id") }

        notification.isRead = true

        val updatedNotification = notificationRepository.save(notification)

        return mapToNotificationResponse(updatedNotification)
    }

    @Transactional
    fun markAllAsRead(userId: Long): Int {
        log.info("Marking all notifications as read for user: $userId")

        val unreadNotifications = notificationRepository.findByUserIdAndIsRead(userId, false, Pageable.unpaged())

        unreadNotifications.forEach { notification ->
            notification.isRead = true
            notificationRepository.save(notification)
        }

        return unreadNotifications.numberOfElements
    }

    @Transactional(readOnly = true)
    fun getNotificationCount(userId: Long): NotificationCountResponse {
        log.info("Counting notifications for user: $userId")

        val totalCount = notificationRepository.countByUserId(userId, Pageable.unpaged()).totalElements
        val unreadCount = notificationRepository.countByUserIdAndIsRead(userId, false)

        return NotificationCountResponse(
            total = totalCount,
            unread = unreadCount
        )
    }

    private fun sendWebSocketNotification(notification: Notification) {
        val webSocketNotification = WebSocketNotification(
            id = notification.id,
            userId = notification.userId,
            title = notification.title,
            content = notification.content,
            type = notification.type,
            relatedEntityId = notification.relatedEntityId,
            createdAt = notification.createdAt
        )

        // Send to user-specific topic
        messagingTemplate.convertAndSend(
            "/topic/notifications/${notification.userId}",
            webSocketNotification
        )

        log.info("Sent WebSocket notification to user: ${notification.userId}")
    }

    private fun mapToNotificationResponse(notification: Notification): NotificationResponse {
        return NotificationResponse(
            id = notification.id,
            title = notification.title,
            content = notification.content,
            type = notification.type,
            isRead = notification.isRead,
            relatedEntityId = notification.relatedEntityId,
            createdAt = notification.createdAt
        )
    }

    private fun mapToPagedResponse(page: Page<Notification>): PagedResponse<NotificationResponse> {
        val notifications = page.content.map { mapToNotificationResponse(it) }

        return PagedResponse(
            content = notifications,
            pageNumber = page.number,
            pageSize = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            lastPage = page.isLast
        )
    }
}