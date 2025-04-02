package com.teamconnect.matchingservice.model

import com.teamconnect.common.model.BaseEntity
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "matches")
data class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val sourceUserId: Long,

    @Column(nullable = false)
    val targetUserId: Long,

    @Column(nullable = false)
    val score: Double,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: MatchStatus = MatchStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity()

enum class MatchStatus {
    PENDING, ACCEPTED, REJECTED
}