package com.hostiflix

import com.hostiflix.controller.AuthenticationController
import com.hostiflix.services.AuthenticationService
import com.nhaarman.mockito_kotlin.given
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(MockitoJUnitRunner::class)
@WebMvcTest
class AuthenticationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    lateinit var authenticationService: AuthenticationService

    @InjectMocks
    lateinit var authenticationController: AuthenticationController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(authenticationController)
            .build()
    }

    @Test
    fun `should return new redirect url for github`() {
        /* Given */
        val githubRedirectUrl = "githubRedirectUrl"
        given(authenticationService.buildNewRedirectUrlForGithub()).willReturn(githubRedirectUrl)

        /* When, Then */
        mockMvc
            .perform(get("/auth/login"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.redirectUrlGithub", `is` (githubRedirectUrl)))
    }

    @Test
    fun `should return access token`() {
        /* Given */
        val githubLoginCode = "githubLoginCode"
        val githubLoginState = "githubLoginState"
        val accessToken = "accessToken"
        given(authenticationService.authenticateOnGithubAndReturnAccessToken(githubLoginCode,githubLoginState)).willReturn(accessToken)

        /* When, Then */
        mockMvc
            .perform(get("/auth/redirect")
                .param("code", githubLoginCode)
                .param("state", githubLoginState))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken", `is` (accessToken)))
    }

    @Test
    fun `should return 400 if access token is null`() {
        /* Given */
        val githubLoginCode = "githubLoginCode"
        val githubLoginState = "githubLoginState"
        given(authenticationService.authenticateOnGithubAndReturnAccessToken(githubLoginCode,githubLoginState)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(get("/auth/redirect")
                .param("code", githubLoginCode)
                .param("state", githubLoginState))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is` ("states don't match")))
    }
}