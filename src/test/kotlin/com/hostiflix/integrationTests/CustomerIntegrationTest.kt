package com.hostiflix.integrationTests

import com.hostiflix.entity.Customer
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class CustomerIntegrationTest: BaseIntegrationTest() {

    @Before
    fun setUp() {
        RestAssured.basePath = "/customers"
    }

    @Test
    fun `should return a list of all customers`() {
        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("customers", hasSize<Customer>(1))
    }

    @Test
    fun `should return a customer by id`() {
        val mockCustomer = MockData.customer("2")
        val customer = customerRepository.save(mockCustomer)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/${customer.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("id", `is`(customer.id))
            .body("name", `is`(customer.name))
            .body("email", `is`(customer.email))
            .body("githubUsername", `is`(customer.githubUsername))
            .body("githubId", `is`(customer.githubId))
    }

    @Test
    fun `should return updated customer`() {
        val mockCustomer = MockData.customer("3")
        val customer = customerRepository.save(mockCustomer)
        val updatedCustomer = customer.apply {
            name = "updated"
            email = "updated"
        }
        val body = objectMapper.writeValueAsString(updatedCustomer)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .put()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("id", `is`(customer.id))
            .body("name", `is`("updated"))
            .body("email", `is`("updated"))
            .body("githubUsername", `is`(customer.githubUsername))
            .body("githubId", `is`(customer.githubId))

        val customerResult = customerRepository.findById(customer.id!!)
        assertThat(customerResult.get().id).isEqualTo(customer.id)
        assertThat(customerResult.get().name).isEqualTo("updated")
        assertThat(customerResult.get().email).isEqualTo("updated")
    }

    @Test
    fun `should return no content`() {
        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .delete("/${testCustomer!!.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NO_CONTENT.value())

        val customerList = customerRepository.findAll().toList()
        assertThat(customerList.size).isEqualTo(0)
    }
}