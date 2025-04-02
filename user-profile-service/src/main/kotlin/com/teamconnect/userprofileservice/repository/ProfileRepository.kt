package com.teamconnect.userprofileservice.repository

import com.teamconnect.userprofileservice.model.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, Long> {
    @Query("SELECT p FROM Profile p JOIN p.skills s WHERE s = :skill AND p.privacySettings.isSkillsPublic = true")
    fun findBySkillsContaining(skill: String): List<Profile>

    fun findByDepartment(department: String): List<Profile>
}