package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
data class Branch(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,

    val name: String,

    val subDomain: String
) {
    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "branch"
    )
    @JsonManagedReference
    @get:JsonProperty
    @field:JsonIgnore
    @set:JsonIgnore
    var jobs: MutableList<Job> = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    lateinit var project: Project
}