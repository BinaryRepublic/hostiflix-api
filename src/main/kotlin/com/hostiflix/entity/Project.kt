package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
data class Project(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,

    @JsonIgnore
    var customerId: String? = null,

    val hash: String,

    var name: String,

    var repositoryOwner: String,

    var repositoryName: String,

    val type: String,

    val startCode: String,

    val buildCode: String,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "project"
    )
    @JsonManagedReference
    @NotEmpty
    var branches: List<Branch>
) {
    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
    }
}

enum class ProjectType {
    NODEJS
}