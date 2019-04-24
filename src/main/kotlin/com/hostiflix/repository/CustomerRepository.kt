package com.hostiflix.repository

import com.hostiflix.entity.Customer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CrudRepository<Customer, String> {

    // SELECT CASE WHEN EXISTS ( SELECT * FROM customer WHERE github_id=githubId) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    fun existsByGithubId(githubId : String) : Boolean

    // SELECT * FROM customer WHERE github_id=githubId;
    fun findCustomerByGithubId(githubId: String) : Customer
}