package com.teamconnect.matchingservice.repository

import com.teamconnect.matchingservice.model.Match
import com.teamconnect.matchingservice.model.MatchStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MatchRepository : JpaRepository<Match, Long> {
    fun findBySourceUserIdAndStatus(userId: Long, status: MatchStatus): List<Match>
    fun findByTargetUserIdAndStatus(userId: Long, status: MatchStatus): List<Match>
    fun findBySourceUserIdAndTargetUserId(sourceUserId: Long, targetUserId: Long): Optional<Match>
}