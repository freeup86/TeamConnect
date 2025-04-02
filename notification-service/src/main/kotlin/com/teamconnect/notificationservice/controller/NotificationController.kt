package com.teamconnect.notificationservice.controller

import com.teamconnect.common.util.logger
import com.teamconnect.notificationservice.dto.CreateNotificationRequest
import com.teamconnect.notificationservice.dto.NotificationCountResponse
import com.teamconnect.notificationservice.dto.NotificationResponse
import com.teamconnect.notificationservice.dto.PagedResponse
import com.teamconnect.notificationservice.model.NotificationType
import com.teamconnect.notificationservice.service.NotificationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
class NotificationController(
    private val notificationService: NotificationService
) {
    private val log = logger()

    @PostMapping
    fun createNotification(
        @Valid @RequestBody request: CreateNotificationRequest
    ): ResponseEntity<NotificationResponse> {
        log.info("Received request to create notification for user: ${request.userId}")
        val notification = notificationService.createNotification(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(notification)
    }

    @GetMapping("/{id}")
    fun getNotification(@PathVariable id: Long): ResponseEntity<NotificationResponse> {
        log.info("Received request to get notification with id: $id")
        val notification = notificationService.getNotificationById(id)
        return ResponseEntity.ok(notification)
    }

    @GetMapping
    fun getUserNotifications(
        @RequestHeader("X-User-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        log.info("Received request to get notifications for user: $userId")
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val notifications = notificationService.getUserNotifications(userId, pageable)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread")
    fun getUnreadNotifications(
        @RequestHeader("X-User-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        log.info("Received request to get unread notifications for user: $userId")
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val notifications = notificationService.getUnreadNotifications(userId, pageable)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/type/{type}")
    fun getNotificationsByType(
        @RequestHeader("X-User-ID") userId: Long,
        @PathVariable type: NotificationType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        log.info("Received request to get notifications for user: $userId, type: $type")
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val notifications = notificationService.getNotificationsByType(userId, type, pageable)
        return ResponseEntity.ok(notifications)
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: Long
    ): ResponseEntity<NotificationResponse> {
        log.info("Received request to mark notification as read: $id")
        val notification = notificationService.markAsRead(id)
        return ResponseEntity.ok(notification)
    }

    @PutMapping("/read-all")
    fun markAllAsRead(
        @RequestHeader("X-User-ID") userId: Long
    ): ResponseEntity<Map<String, Int>> {
        log.info("Received request to mark all notifications as read for user: $userId")
        val count = notificationService.markAllAsRead(userId)
        return ResponseEntity.ok(mapOf("markedAsRead" to count))
    }

    @GetMapping("/count")
    fun getNotificationCount(
        @RequestHeader("X-User-ID") userId: Long
    ): ResponseEntity<NotificationCountResponse> {
        log.info("Received request to count notifications for user: $userId")
        val countResponse = notificationService.getNotificationCount(userId)
        return ResponseEntity.ok(countResponse)
    }
}