package com.hostiflix.integrationTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CustomerIntegrationTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${local.server.port}")
    private val serverPort: Int = 0

    lateinit var customer: Customer
    val accessToken = "accessToken"

    @Before
    fun setUp() {
        RestAssured.port = serverPort
        RestAssured.basePath = "/customers"
    }

    @After
    fun clearDatabase() {
        customerRepository.deleteAll()
    }

    @Test
    fun `1_should return a list of all customers`() {
        val mockCustomer = MockData.customer("1")
        customer = customerRepository.save(mockCustomer)

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
    fun `2_should return a customer by id`() {
        val mockCustomer = MockData.customer("2")
        customer = customerRepository.save(mockCustomer)

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
    fun `3_should return updated customer`() {
        val mockCustomer = MockData.customer("3")
        customer = customerRepository.save(mockCustomer)
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
    fun `4_should return no content`() {
        val mockCustomer = MockData.customer("4")
        customer = customerRepository.save(mockCustomer)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .delete("/${customer.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NO_CONTENT.value())

        val customerList = customerRepository.findAll().toList()
        assertThat(customerList.size).isEqualTo(0)
    }
}