package com.hostiflix.service

import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
        private val customerRepository: CustomerRepository
) {
    // SELECT * FROM customer;
    fun findAllCustomers() = customerRepository.findAll().toList()

    // SELECT * FROM customer WHERE id=id
    fun findCustomerById(id: String): Customer? {
        val customer = customerRepository.findById(id)
        return customer.takeIf { it.isPresent }?.get()
    }

    // INSERT INTO customer (customer.id, customer.name, customer.email, customer.githubUsername, customer.githubId);
    fun createCustomer(customer: Customer) = customerRepository.save(customer)

    // SELECT CASE WHEN EXISTS (SELECT * FROM customer WHERE id=id) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    fun existsById(id: String) = customerRepository.existsById(id)

    // DELETE FROM customer WHERE id=id;
    fun deleteCustomer(id: String) = customerRepository.deleteById(id)

    fun existsByGithubId(githubId : String) = customerRepository.existsByGithubId(githubId)

    fun findCustomerByGithubId(githubId: String) = customerRepository.findCustomerByGithubId((githubId))
}