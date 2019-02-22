package com.projectcloud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomerControllerApplication

fun main(args: Array<String>) {
	runApplication<CustomerControllerApplication>(*args)
}

