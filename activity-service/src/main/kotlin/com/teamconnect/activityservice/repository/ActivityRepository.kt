package com.teamconnect.activityservice.repository

import com.teamconnect.activityservice.model.Activity
import com.teamconnect.activityservice.model.ActivityStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {
    fun findByCreatedBy(userId: Long, pageable: Pageable): Page<Activity>

    @Query("SELECT a FROM Activity a JOIN a.participants p WHERE p.userId = :userId")
    fun findByParticipantId(userId: Long, pageable: Pageable): Page<Activity>

    @Query("SELECT a FROM Activity a JOIN a.requiredSkills s WHERE s = :skill")
    fun findByRequiredSkillsContaining(skill: String, pageable: Pageable): Page<Activity>

    fun findByScheduledAtBetweenAndStatus(
        start: LocalDateTime,
        end: LocalDateTime,
        status: ActivityStatus,
        pageable: Pageable
    ): Page<Activity>
}