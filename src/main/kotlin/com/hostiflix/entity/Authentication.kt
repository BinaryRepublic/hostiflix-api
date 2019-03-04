package com.hostiflix.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Authentication(
    @Id
    val githubAccessToken: String,
    val customerId: String,
    var latest: Boolean
)
