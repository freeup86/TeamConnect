package com.teamconnect.activityservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
class ActivityServiceApplication

fun main(args: Array<String>) {
	runApplication<ActivityServiceApplication>(*args)
}