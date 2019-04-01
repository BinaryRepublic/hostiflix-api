package com.hostiflix.integrationTests

import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthCredentialsRepository
import com.hostiflix.repository.CustomerRepository
import com.hostiflix.repository.GithubLoginStateRepository
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthenticationIntegrationTest {

    @Autowired
    private lateinit var githubLoginStateRepository: GithubLoginStateRepository

    @Autowired
    private lateinit var authCredentialsRepository: AuthCredentialsRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Value("\${local.server.port}")
    private val serverPort: Int = 0

    @Before
    fun setUp() {
        RestAssured.port = serverPort
        RestAssured.basePath = "/auth"
    }

    @After
    fun clearDatabase() {
        githubLoginStateRepository.deleteAll()
        customerRepository.deleteAll()
        authCredentialsRepository.deleteAll()
    }

    @Test
    fun `should return github redirect url`() {
        RestAssured
            .given()
            .get("/login")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("redirectUrlGithub", notNullValue())

        val githubLoginStateList = githubLoginStateRepository.findAll().toList()
        assertThat(githubLoginStateList.size).isEqualTo(1)
    }

    @Test
    fun `should return access token`() {
        val newState = GithubLoginState()
        val stateId = githubLoginStateRepository.save(newState).id
        val code = "code"

        RestAssured
            .given()
            .param("code", code)
            .param("state", stateId)
            .get("/getAccessToken")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("accessToken", `is` ("accessToken"))

        val githubLoginStateList = githubLoginStateRepository.findAll().toList()
        assertThat(githubLoginStateList.size).isEqualTo(0)

        val customerList = customerRepository.findAll().toList()
        assertThat(customerList.size).isEqualTo(1)

        val authCredentialsList = authCredentialsRepository.findAll().toList()
        assertThat(authCredentialsList.size).isEqualTo(1)
    }

    @Test
    fun `should return null because state doesn't exist in db`() {
        val code = "code"
        val state = "state"

        RestAssured
            .given()
            .param("code", code)
            .param("state", state)
            .get("/getAccessToken")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("error", `is` ("states don't match"))
    }
}