package com.hostiflix.integrationTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.Customer
import com.hostiflix.entity.Project
import com.hostiflix.repository.AuthCredentialsRepository
import com.hostiflix.repository.CustomerRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.PostgreSQLContainer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
abstract class BaseIntegrationTest {

    companion object {
        @ClassRule
        @JvmField
        var postgreSQLContainer: PostgreSQLContainer<*> = KPostgreSQLContainer()
                .withPassword("password")
                .withUsername("postgres")
    }

    object Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            val jdbcUrl = postgreSQLContainer.getJdbcUrl()
            val values = TestPropertyValues.of(
                    "spring.datasource.url=$jdbcUrl",
                    "spring.datasource.password=" + BaseIntegrationTest.postgreSQLContainer.getPassword(),
                    "spring.datasource.username=" + BaseIntegrationTest.postgreSQLContainer.getUsername()
            )
            values.applyTo(configurableApplicationContext)
        }
    }

    @Value("\${local.server.port}")
    val serverPort: Int = 0

    @Autowired
    lateinit var authCredentialsRepository: AuthCredentialsRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

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
        authCredentialsRepository.deleteAll()
        customerRepository.deleteAll()
        githubLoginStateRepository.deleteAll()
    }

    fun saveTestCustomerWithAuthCredentials(customerId: String, accessToken: String): Customer {
        val customer = customerRepository.save(MockData.customer("c1"))
        authCredentialsRepository.save(AuthCredentials("ac1", accessToken, customer.id!!,  true))
        return customer
    }
}