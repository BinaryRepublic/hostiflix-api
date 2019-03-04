package com.hostiflix.repository

import com.hostiflix.entity.Authentication
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationRepository : CrudRepository<Authentication, String> {
    fun findByCustomerIdAndLatest(customerId: String, latest: Boolean): Authentication
}