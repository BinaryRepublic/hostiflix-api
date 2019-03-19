package com.hostiflix

import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.services.AuthenticationService
import com.hostiflix.services.CustomerService
import com.hostiflix.webservices.GithubWs
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


@SpringBootTest
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner::class)
@TestPropertySource(properties = [("github.login.base=githubLoginBase"), ("github.login.redirect=githubLoginRedirect&state={state}&scope={scope}")])
class AuthenticationServiceTest {

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    @Mock
    private lateinit var githubLoginStateRepository: GithubLoginStateRepository

    @Mock
    private lateinit var customerService: CustomerService

    @Mock
    private lateinit var githubWs: GithubWs

    @InjectMocks
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun `should return github url`() {

        /* Given */

        val githubLoginState = "githubLoginState"

        val spyAuthenticationService = Mockito.spy(authenticationService)

        doReturn(githubLoginState).`when`(spyAuthenticationService).createAndStoreNewGithubState()


        /* When */

        val githubRedirectUrl = authenticationService.buildNewRedirectUrlForGithub()

        /* Then */
        assertThat(githubRedirectUrl).isNotNull()
        // verify(githubLoginStateRepository).save(githubLoginState)
    }
}