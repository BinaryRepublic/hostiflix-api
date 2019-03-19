package com.hostiflix

import com.hostiflix.config.GithubConfig
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.services.AuthenticationService
import com.hostiflix.services.CustomerService
import com.hostiflix.webservices.GithubWs
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.SpringBootConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.util.ReflectionTestUtils


@SpringBootTest
@SpringBootConfiguration
@RunWith(MockitoJUnitRunner::class)
class AuthenticationServiceTest {

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    @Mock
    private lateinit var githubLoginStateRepository: GithubLoginStateRepository

    @Mock
    private lateinit var customerService: CustomerService

    @Mock
    private lateinit var githubWs: GithubWs

    @Mock
    private lateinit var githubConfig: GithubConfig

    @InjectMocks
    private lateinit var authenticationService: AuthenticationService

    @Before
    fun setup() {
        given(githubConfig.loginBase).willReturn("http://github.com/")
        given(githubConfig.loginRedirect).willReturn("redirect?state={state}&scope={scope}")
        given(githubConfig.loginGetAccessToken).willReturn("1234")
    }

    @Test
    fun `should return github url`() {
        /* Given */
        val stateId = "id"
        given(githubLoginStateRepository.save<GithubLoginState>(any())).willReturn(GithubLoginState(stateId))

        /* When */
        val githubRedirectUrl = authenticationService.buildNewRedirectUrlForGithub()

        /* Then */
        assertThat(githubRedirectUrl).isEqualTo("http://github.com/redirect?state=id&scope=REPO,USER")
    }
}