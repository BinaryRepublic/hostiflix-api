package com.hostiflix.controller

import com.hostiflix.entity.Customer
import com.hostiflix.services.CustomerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val customerService: CustomerService
) {

    @GetMapping
    fun findAll(): ResponseEntity<*> {
        val customerList = customerService.findAllCustomers()

        return ResponseEntity.ok().body(hashMapOf("customers" to customerList))
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable
        id: String
    ): ResponseEntity<*> {
        val customer = customerService.findCustomerById(id)

        return if (customer !== null) {
            ResponseEntity.ok().body(customer)
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "Customer ID not found"))
        }
    }

    @PutMapping
    fun update(
        @RequestBody
        newCustomer: Customer
    ): ResponseEntity<*> {
        return if (customerService.existsById(newCustomer.id)){
            customerService.createCustomer(newCustomer)
            ResponseEntity.ok().body(newCustomer)
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "Customer ID not found"))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable
        id: String
    ): ResponseEntity<Void> {
        customerService.deleteCustomer(id)

        return ResponseEntity.noContent().build()
    }
}