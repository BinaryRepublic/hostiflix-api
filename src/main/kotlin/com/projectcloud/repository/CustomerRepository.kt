package com.projectcloud.repository

import com.projectcloud.entity.Customer
import com.projectcloud.entity.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CrudRepository<Customer, String>