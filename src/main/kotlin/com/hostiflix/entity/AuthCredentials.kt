package com.hostiflix.entity

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class AuthCredentials(
    val githubAccessToken: String,
    val customerId: String,
    var latest: Boolean
) {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    lateinit var id: String
}
