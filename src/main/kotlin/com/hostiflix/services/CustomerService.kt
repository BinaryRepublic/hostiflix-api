package com.hostiflix.services

import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
        private val customerRepository: CustomerRepository
) {
    fun findAllCustomers() : List<Customer> = customerRepository.findAll().toList()

    fun findCustomerById(id: String): Customer? {
        val customer = customerRepository.findById(id)
        return customer.takeIf { it.isPresent }?.get()
    }

    fun createCustomer(customer: Customer) : Customer? = customerRepository.save(customer)

    fun existsById(id: String) = customerRepository.existsById(id)

    fun deleteCustomer(id: String) = customerRepository.deleteById(id)

    fun existsByGithubId(githubId : String) : Boolean  = customerRepository.existsByGithubId(githubId)

    fun findCustomerByGithubId(githubId: String) : Customer = customerRepository.findCustomerByGithubId((githubId))
}