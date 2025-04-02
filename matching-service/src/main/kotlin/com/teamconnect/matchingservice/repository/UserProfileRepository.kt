package com.teamconnect.matchingservice.repository

import com.teamconnect.matchingservice.model.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    fun findBySkillsContaining(skill: String): List<UserProfile>
    fun findByDepartment(department: String): List<UserProfile>
}