package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class Branch(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    val id: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    var project: Project,
    val name: String
)
