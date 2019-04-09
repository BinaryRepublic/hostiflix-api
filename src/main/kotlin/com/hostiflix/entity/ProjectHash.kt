package com.hostiflix.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.PrePersist

@Entity
data class ProjectHash(
    @Id
    val id: String
) {
    @Column(nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
    }
}