package com.teamconnect.userprofileservice.model

import com.teamconnect.common.model.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    val userId: Long,

    @Column(nullable = false)
    var department: String,

    @ElementCollection
    @CollectionTable(
        name = "profile_skills",
        joinColumns = [JoinColumn(name = "profile_id")]
    )
    @Column(name = "skill")
    var skills: MutableSet<String> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(
        name = "profile_interests",
        joinColumns = [JoinColumn(name = "profile_id")]
    )
    @Column(name = "interest")
    var interests: MutableSet<String> = mutableSetOf(),

    @Column(nullable = false)
    var careerStage: String,

    @ElementCollection
    @CollectionTable(
        name = "profile_learning_goals",
        joinColumns = [JoinColumn(name = "profile_id")]
    )
    @Column(name = "learning_goal")
    var learningGoals: MutableSet<String> = mutableSetOf(),

    @Embedded
    var privacySettings: PrivacySettings = PrivacySettings()
) : BaseEntity()

@Embeddable
data class PrivacySettings(
    var isProfilePublic: Boolean = true,
    var isSkillsPublic: Boolean = true,
    var isInterestsPublic: Boolean = true
)