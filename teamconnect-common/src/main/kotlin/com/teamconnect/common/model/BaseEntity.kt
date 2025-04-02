package com.teamconnect.common.model

import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate

@MappedSuperclass
abstract class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        private set

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
        private set

    @PrePersist
    fun onPersist() {
        createdAt = LocalDateTime.now()
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}