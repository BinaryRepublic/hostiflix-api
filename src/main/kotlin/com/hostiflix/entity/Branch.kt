package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class Branch(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,

    val name: String,

    val subDomain: String,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "branch"
    )
    @JsonManagedReference
    var jobs: List<Job> = emptyList()
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    lateinit var project: Project
}