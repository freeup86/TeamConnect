package com.teamconnect.activityservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "authentication-service", path = "/api/auth")
interface UserServiceClient {

    @GetMapping("/users/{id}/basic")
    fun getUserBasicInfo(@PathVariable id: Long): UserBasicInfoDto
}

data class UserBasicInfoDto(
    val id: Long,
    val name: String,
    val email: String
)