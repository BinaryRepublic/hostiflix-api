package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
data class Project(
    val customerId: String,
    val name: String,
    val repository: String,
    val projectType: String,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "project"
    )
    @JsonManagedReference
    var branches: List<Branch>
){
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    lateinit var id: String
}