package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
class Job (
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,

    val status: JobStatus,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    var finishedAt: Instant? = null
) {
    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    lateinit var branch: Branch
}

enum class JobStatus {
    BUILD_SCHEDULED,
    BUILD_FAILED,
    DEPLOYMENT_PENDING,
    DEPLOYMENT_SUCCESSFUL,
    DEPLOYMENT_FAILED
}