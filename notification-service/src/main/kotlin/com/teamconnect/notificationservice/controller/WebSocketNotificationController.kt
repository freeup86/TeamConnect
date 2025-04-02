package com.teamconnect.notificationservice.controller

import com.teamconnect.common.util.logger
import com.teamconnect.notificationservice.dto.WebSocketNotification
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WebSocketNotificationController(
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val log = logger()

    @MessageMapping("/connect")
    @SendTo("/topic/status")
    fun handleConnect(message: Map<String, Any>): Map<String, Any> {
        val userId = message["userId"]
        log.info("WebSocket connection established for user: $userId")
        return mapOf("status" to "connected", "userId" to userId)
    }

    fun sendNotification(userId: Long, notification: WebSocketNotification) {
        log.info("Sending WebSocket notification to user: $userId")
        messagingTemplate.convertAndSend("/topic/notifications/$userId", notification)
    }
}