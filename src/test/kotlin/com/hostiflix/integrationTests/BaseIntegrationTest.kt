package com.hostiflix.integrationTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.Customer
import com.hostiflix.entity.Project
import com.hostiflix.repository.*
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
// SE_03 abstract classes
abstract class BaseIntegrationTest {

    @Value("\${local.server.port}")
    val serverPort: Int = 0

    @Autowired
    lateinit var authCredentialsRepository: AuthCredentialsRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var projectHashRepository: ProjectHashRepository

    @Autowired
    lateinit var jobRepository: JobRepository

    @Autowired
    lateinit var githubLoginStateRepository: GithubLoginStateRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var project : Project
    val accessToken = "accessToken"

    var testCustomer: Customer? = null

    @Before
    fun globalSetUp() {
        RestAssured.port = serverPort
        testCustomer = saveTestCustomerWithAuthCredentials("c1", accessToken)
    }

    @After
    fun clearDatabase() {
        projectRepository.deleteAll()
        projectHashRepository.deleteAll()
        authCredentialsRepository.deleteAll()
        customerRepository.deleteAll()
        githubLoginStateRepository.deleteAll()
    }

    fun saveTestCustomerWithAuthCredentials(customerId: String, accessToken: String): Customer {
        // SE_03 type inference
        val customer = customerRepository.save(MockData.customer("c1"))
        authCredentialsRepository.save(AuthCredentials("ac1", accessToken, customer.id!!,  true))
        return customer
    }
}