package com.hostiflix.controller

import com.hostiflix.dto.GithubRedirectEnvironment
import com.hostiflix.config.JsonConfig
import com.hostiflix.service.AuthenticationService
import com.nhaarman.mockito_kotlin.given
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JsonConfig::class])
@WebMvcTest
class AuthenticationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jackson2HttpMessageConverter: MappingJackson2HttpMessageConverter

    @Mock
    lateinit var authenticationService: AuthenticationService

    @InjectMocks
    lateinit var authenticationController: AuthenticationController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(authenticationController)
            .setMessageConverters(this.jackson2HttpMessageConverter)
            .build()
    }

    @Test
    fun `should return new redirect url for github`() {
        /* Given */
        val githubRedirectUrl = "githubRedirectUrl/PRODUCTION"
        given(authenticationService.buildGithubAuthorizeUrl(GithubRedirectEnvironment.PRODUCTION)).willReturn(githubRedirectUrl)

        /* When, Then */
        mockMvc
            .perform(get("/auth/login"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.githubAuthorizeUrl", `is` (githubRedirectUrl)))
    }

    @Test
    fun `should return access token`() {
        /* Given */
        val code = "code"
        val state = "state"
        val accessToken = "accessToken"
        given(authenticationService.authenticateOnGithubAndReturnAccessToken(code,state)).willReturn(accessToken)

        /* When, Then */
        mockMvc
            .perform(get("/auth/getAccessToken")
                .param("code", code)
                .param("state", state))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken", `is` (accessToken)))
    }

    @Test
    fun `should return error 400 if access token is null`() {
        /* Given */
        val code = "code"
        val state = "state"
        given(authenticationService.authenticateOnGithubAndReturnAccessToken(code,state)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(get("/auth/getAccessToken")
                .param("code", code)
                .param("state", state))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is` ("states don't match")))
    }
}