package com.hostiflix

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.Customer
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.services.AuthenticationService
import com.hostiflix.services.CustomerService
import com.hostiflix.webservices.GithubWs
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.check
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.SpringBootConfiguration

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

    @Test
    fun `should create and store new github state`() {
        /* Given */
        val testStateId = "id"
        given(githubLoginStateRepository.save<GithubLoginState>(any())).willReturn(GithubLoginState(testStateId))

        /* When */
        val stateId = authenticationService.createAndStoreNewGithubState()

        /* Then */
        assertThat(stateId).isEqualTo(testStateId)
    }

    @Test
    fun `should return null when state doesn't exist in database`() {
        /* Given */
        val code = "code"
        val state = "state"
        given(githubLoginStateRepository.existsById(any())).willReturn(false)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        assertThat(accessTokenResult).isEqualTo(null)
    }

    @Test
    fun `should authenticate on github and store new customer and return access token`() {
        /* Given */
        val code = "code"
        val state = "state"
        val accessToken = "accessToken"
        val githubCustomer = GithubCustomerDto(
            "id1",
            "name1",
            "login1"
        )
        val githubCustomerPrimaryEmail = "primaryEmail1"
        val customer = Customer(
            "name1",
            "email1",
            "githubUserName1",
            "githubId1"
        ).apply { id = "randomString" }

        given(githubLoginStateRepository.existsById(any())).willReturn(true)
        given(githubWs.getAccessToken(code, state)).willReturn(accessToken)
        given(githubWs.getCustomer(accessToken)).willReturn(githubCustomer)
        given(customerService.existsByGithubId(githubCustomer.id)).willReturn(false)
        given(githubWs.getCustomerPrimaryEmail(accessToken)).willReturn(githubCustomerPrimaryEmail)
        given(customerService.findCustomerByGithubId(githubCustomer.id)).willReturn(customer)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        verify(githubLoginStateRepository).deleteById(any())
        verify(customerService).createCustomer(any())
        verify(authenticationRepository).findAll()
        verify(authenticationRepository).save<AuthCredentials>(any())
        assertThat(accessTokenResult).isEqualTo(accessToken)
    }

    @Test
    fun `should authenticate on github and return access token`() {
        /* Given */
        val code = "code"
        val state = "state"
        val accessToken = "accessToken"
        val githubCustomer = GithubCustomerDto(
            "id1",
            "name1",
            "login1"
        )
        val customer = Customer(
            "name1",
            "email1",
            "githubUserName1",
            "githubId1"
        ).apply { id = "randomString" }

        given(githubLoginStateRepository.existsById(any())).willReturn(true)
        given(githubWs.getAccessToken(code, state)).willReturn(accessToken)
        given(githubWs.getCustomer(accessToken)).willReturn(githubCustomer)
        given(customerService.existsByGithubId(githubCustomer.id)).willReturn(true)
        given(customerService.findCustomerByGithubId(githubCustomer.id)).willReturn(customer)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        verify(githubLoginStateRepository).deleteById(any())
        verify(authenticationRepository).findAll()
        verify(authenticationRepository).save<AuthCredentials>(any())
        assertThat(accessTokenResult).isEqualTo(accessToken)
    }

    @Test
    fun `should create and store new customer`() {
        /* Given */
        val githubCustomer = GithubCustomerDto(
            "id2",
            "name2",
            "login2"
        )
        val primaryEmail = "primaryEmail2"

        /* When */
        authenticationService.createAndStoreNewCustomer(githubCustomer, primaryEmail)

        /* Then */
        verify(customerService).createCustomer(check {
            assertThat(it.name).isEqualTo(githubCustomer.name)
            assertThat(it.email).isEqualTo(primaryEmail)
            assertThat(it.githubUsername).isEqualTo(githubCustomer.login)
            assertThat(it.githubId).isEqualTo(githubCustomer.id)
        })
    }

    @Test
    fun `should set all access tokens to latest false`() {
        /* Given */
        val authCredentials1 = AuthCredentials(
            "githubAccessToken1",
            "customerId1",
            false
        )
        val authCredentials2 = AuthCredentials(
            "githubAccessToken2",
            "customerId2",
            true
        )
        val listOfAuthCredentials = listOf(authCredentials1, authCredentials2)
        given(authenticationRepository.findAll()).willReturn(listOfAuthCredentials)

        /* When */
        authenticationService.setAllExistingAccessTokensToLatestFalse()

        /* Then */
        assertThat(listOfAuthCredentials[1].latest).isEqualTo(false)
    }

    @Test
    fun `should create and store new auth credentials`() {
        /* Given */
        val githubId = "githubId2"
        val accessToken = "accessToken"
        val customer = Customer(
            "name2",
            "email2",
            "githubUsername2",
            "githubId2"
        ).apply { id = "id2" }
        given(customerService.findCustomerByGithubId(githubId)).willReturn(customer)

        /* When */
        authenticationService.createAndStoreNewAuthCredentials(githubId, accessToken)

        /* Then */
        verify(authenticationRepository).save<AuthCredentials>(any())
    }
}