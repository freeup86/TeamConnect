package com.teamconnect.notificationservice.service

import com.teamconnect.common.util.logger
import com.teamconnect.notificationservice.dto.CreateNotificationRequest
import com.teamconnect.notificationservice.model.NotificationType
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@EnableScheduling
class ScheduledNotificationService(
    private val notificationService: NotificationService,
    private val restTemplate: RestTemplate
) {
    private val log = logger()

    @Scheduled(cron = "0 0 9 * * ?") // Runs at 9:00 AM every day
    fun sendDailyActivityReminders() {
        log.info("Running scheduled job: sendDailyActivityReminders")

        try {
            // This would normally call the Activity Service to get activities scheduled for today
            // For simplicity, we're just simulating it here

            // In a real implementation, you would:
            // 1. Call Activity Service to get activities scheduled for today
            // 2. For each activity, send a reminder to all participants

            log.info("Successfully sent daily activity reminders")
        } catch (e: Exception) {
            log.error("Error sending daily activity reminders", e)
        }
    }

    // Example of how you would send reminder notifications
    private fun sendActivityReminder(activityId: Long, userId: Long, activityTitle: String, scheduledTime: LocalDateTime) {
        val formattedTime = scheduledTime.format(DateTimeFormatter.ofPattern("h:mm a"))

        notificationService.createNotification(
            CreateNotificationRequest(
                userId = userId,
                title = "Activity Reminder",
                content = "Reminder: Your activity '$activityTitle' is scheduled for today at $formattedTime.",
                type = NotificationType.ACTIVITY_REMINDER,
                relatedEntityId = activityId
            )
        )
    }
}