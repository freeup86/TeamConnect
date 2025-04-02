package com.teamconnect.matchingservice.model

import com.teamconnect.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_profiles")
data class UserProfile(
    @Id
    val userId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val department: String,

    @ElementCollection
    @CollectionTable(
        name = "user_profile_skills",
        joinColumns = [JoinColumn(name = "user_profile_id")]
    )
    @Column(name = "skill")
    val skills: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(
        name = "user_profile_interests",
        joinColumns = [JoinColumn(name = "user_profile_id")]
    )
    @Column(name = "interest")
    val interests: Set<String> = emptySet(),

    @Column(nullable = false)
    val careerStage: String
) : BaseEntity()