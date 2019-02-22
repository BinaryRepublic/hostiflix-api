package com.projectcloud.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
data class Project(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String,
    val customerId: String?,
    val name: String?,
    val repository: String?,
    val projectType: String?,
    val location: String?,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "project"
    )
    @JsonManagedReference
    val branches: List<Branch>
)