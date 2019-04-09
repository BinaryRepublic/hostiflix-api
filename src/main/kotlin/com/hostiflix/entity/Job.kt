package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.Instant
import javax.persistence.*

@Entity
data class Job (
    @Id
    var id: String,

    @Enumerated(EnumType.STRING)
    var status: JobStatus,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    var finishedAt: Instant? = null
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    lateinit var branch: Branch

    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
    }
}

    enum class JobStatus {
    BUILD_SCHEDULED,
    BUILD_FAILED,
    DEPLOYMENT_PENDING,
    DEPLOYMENT_SUCCESSFUL,
    DEPLOYMENT_FAILED
}