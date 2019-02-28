package com.hostiflix.controller


import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
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

        val customer = customerRepository.findById(id)
        return if (customer.isPresent) {
            ResponseEntity.ok().body(customer.get())
        } else {
            ResponseEntity<Customer>(HttpStatus.NOT_FOUND)
        }
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