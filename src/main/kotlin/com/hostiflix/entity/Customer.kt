package com.hostiflix.entity

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Customer(
	var name: String,
	var email: String,
	var githubUsername: String,
	val githubId: String
){
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	lateinit var id: String

	override fun toString(): String {
		return "{\"id\" : \"$id\", \"name\" : \"$name\", \"email\" : \"$email\", \"githubUsername\" : \"$githubUsername\", \"githubId\" : \"$githubId\"}"
	}
}
