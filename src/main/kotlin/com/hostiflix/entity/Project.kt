package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
data class Project(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,

    @JsonIgnore
    var customerId: String? = null,

    @NotNull
    var name: String,

    @NotNull
    val repository: String,
    val projectType: String,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "project"
    )
    @JsonManagedReference
    @NotEmpty
    var branches: List<Branch>
)

enum class ProjectType {
    NODEJS
}