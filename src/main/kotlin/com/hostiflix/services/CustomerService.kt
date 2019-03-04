package com.hostiflix.services

import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
        private val customerRepository: CustomerRepository
) {

    fun checkGithubId(githubId : String) : Boolean {
        return customerRepository.existsByGithubId(githubId)
    }


    fun createCustomer(newCustomer: Customer): Customer {
        customerRepository.save(newCustomer)

        return newCustomer
    }

    fun findCustomerByGithubId(githubId: String) : Customer {
        return customerRepository.findCustomerByGithubId(githubId)
    }

}