package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class Branch(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,
    val name: String
){
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    lateinit var project: Project
}