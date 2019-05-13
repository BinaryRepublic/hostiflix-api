package com.hostiflix.integrationTests

import com.hostiflix.entity.GithubLoginState
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class AuthenticationIntegrationTest: BaseIntegrationTest() {

    @Before
    // SE_03 dynamic dispatch
    override fun globalSetUp() {
        RestAssured.port = serverPort
        RestAssured.basePath = "/auth"
    }

    @Test
    fun `should return github redirect url`() {
        RestAssured
            .given()
            .get("/login")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("githubAuthorizeUrl", notNullValue())

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