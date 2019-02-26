package com.projectcloud.controller


import com.projectcloud.entity.Customer
import com.projectcloud.repository.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/customers")
class CustomerController(
    private val customerRepository: CustomerRepository
) {


    @GetMapping
    fun findAll(): ResponseEntity<*> {

        val customerList = customerRepository.findAll()

        return ResponseEntity.ok().body(customerList)
    }


    @GetMapping("/{id}")
    fun findById(
        @PathVariable
        id: String
    ): ResponseEntity<*> {

        return if (customerRepository.existsById(id)) {
            val customer = customerRepository.findById(id)
            ResponseEntity.ok().body(customer)
        } else {
            ResponseEntity<Customer>(HttpStatus.NOT_FOUND)
        }
    }


    @PostMapping
    fun create(
        @RequestBody
        newCustomer : Customer
    ): ResponseEntity<*> {

        customerRepository.save(newCustomer)

        return ResponseEntity.status(201).body(newCustomer)
    }


    @PutMapping
    fun update(
        @RequestBody
        newCustomer: Customer
    ): ResponseEntity<*> {

        return if (customerRepository.existsById(newCustomer.id)){
            customerRepository.save(newCustomer)
            ResponseEntity.ok().body(newCustomer)
        } else {
            ResponseEntity<Customer>(HttpStatus.BAD_REQUEST)
        }
    }


    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable
        id: String
    ): ResponseEntity<*> {

        customerRepository.deleteById(id)

        return ResponseEntity<Customer>(HttpStatus.NO_CONTENT)
    }
}