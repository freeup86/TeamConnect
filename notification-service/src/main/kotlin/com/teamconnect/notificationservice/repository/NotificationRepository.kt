package com.teamconnect.notificationservice.repository

import com.teamconnect.notificationservice.model.Notification
import com.teamconnect.notificationservice.model.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserId(userId: Long, pageable: Pageable): Page<Notification>
    fun findByUserIdAndIsRead(userId: Long, isRead: Boolean, pageable: Pageable): Page<Notification>
    fun findByUserIdAndType(userId: Long, type: NotificationType, pageable: Pageable): Page<Notification>
    fun countByUserIdAndIsRead(userId: Long, isRead: Boolean): Long
}