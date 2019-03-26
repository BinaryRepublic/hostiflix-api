package com.hostiflix.repository

import com.hostiflix.entity.AuthCredentials
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationRepository : CrudRepository<AuthCredentials, String> {

    fun findByCustomerIdAndLatest(customerId: String, latest: Boolean): AuthCredentials

    fun existsByGithubAccessToken(githubAccessToken : String) : Boolean

    fun findByGithubAccessToken(githubAccessToken : String) : AuthCredentials?
}