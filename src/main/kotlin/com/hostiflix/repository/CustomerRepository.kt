package com.hostiflix.repository

import com.hostiflix.entity.Customer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CrudRepository<Customer, String> {

    fun existsByGithubId(githubId : String): Boolean

}