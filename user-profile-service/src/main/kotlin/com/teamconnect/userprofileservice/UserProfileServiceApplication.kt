package com.teamconnect.userprofileservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class UserProfileServiceApplication

fun main(args: Array<String>) {
	runApplication<UserProfileServiceApplication>(*args)
}