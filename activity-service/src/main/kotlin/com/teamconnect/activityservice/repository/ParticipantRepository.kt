package com.teamconnect.activityservice.repository

import com.teamconnect.activityservice.model.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
    fun findByUserId(userId: Long): Optional<Participant>
}