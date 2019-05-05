package com.hostiflix.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
data class Customer(
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    var id: String? = null,
	var name: String,
	var email: String,
	var githubUsername: String,
	val githubId: String
) {
	@JsonIgnore
	@OneToMany(
			fetch = FetchType.LAZY,
			mappedBy = "customerId",
			cascade = [CascadeType.ALL]
	)
	val authCredentials: List<AuthCredentials> = emptyList()
}