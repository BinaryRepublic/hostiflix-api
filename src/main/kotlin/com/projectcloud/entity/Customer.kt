package com.projectcloud.entity

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Customer(
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	val id: String,
	val firstName: String,
	val lastName: String,
	val email: String,
	val githubUsername: String,
	val githubId: String
)
