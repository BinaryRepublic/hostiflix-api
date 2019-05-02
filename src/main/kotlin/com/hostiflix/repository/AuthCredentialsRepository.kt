package com.hostiflix.repository

import com.hostiflix.entity.AuthCredentials
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthCredentialsRepository : CrudRepository<AuthCredentials, String> {

    // SELECT * FROM auth_credentials WHERE customer_id=customerId AND latest=latest;
    fun findByCustomerIdAndLatest(customerId: String, latest: Boolean): AuthCredentials

    // SELECT CASE WHEN EXISTS (SELECT * FROM auth_credentials WHERE github_access_token=githubAccessToken) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    fun existsByGithubAccessToken(githubAccessToken : String) : Boolean

    // SELECT * FROM auth_credentials WHERE github_access_token=githubAccessToken;
    fun findByGithubAccessToken(githubAccessToken : String) : AuthCredentials?

    // SELECT * FROM auth_credentials WHERE customer_id=customerId;
    fun findAllByCustomerId(customerId: String) : List<AuthCredentials>
}