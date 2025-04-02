package com.teamconnect.activityservice.model

import com.teamconnect.common.model.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "activities")
data class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @ElementCollection
    @CollectionTable(
        name = "activity_required_skills",
        joinColumns = [JoinColumn(name = "activity_id")]
    )
    @Column(name = "skill")
    var requiredSkills: MutableSet<String> = mutableSetOf(),

    @Column(nullable = false)
    var maxParticipants: Int,

    @Column(nullable = false)
    var duration: Int, // Duration in minutes

    @ElementCollection
    @CollectionTable(
        name = "activity_learning_outcomes",
        joinColumns = [JoinColumn(name = "activity_id")]
    )
    @Column(name = "learning_outcome")
    var learningOutcomes: MutableSet<String> = mutableSetOf(),

    @Column(nullable = false)
    val createdBy: Long,

    @Column(nullable = false)
    var scheduledAt: LocalDateTime,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ActivityStatus = ActivityStatus.SCHEDULED,

    @ManyToMany
    @JoinTable(
        name = "activity_participants",
        joinColumns = [JoinColumn(name = "activity_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var participants: MutableSet<Participant> = mutableSetOf()
) : BaseEntity()

enum class ActivityStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}

@Entity
@Table(name = "participants")
data class Participant(
    @Id
    val userId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val department: String
)