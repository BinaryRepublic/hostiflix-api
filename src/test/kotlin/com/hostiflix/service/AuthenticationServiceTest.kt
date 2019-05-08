package com.hostiflix.service

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubRedirectEnvironment
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthCredentialsRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.support.MockData
import com.hostiflix.webservice.githubWs.GithubWsImpl
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
class AuthenticationServiceTest {

    @Mock
    private lateinit var authCredentialsRepository: AuthCredentialsRepository

    @Mock
    private lateinit var githubLoginStateRepository: GithubLoginStateRepository

    @Mock
    private lateinit var customerService: CustomerService

    @Mock
    private lateinit var githubWs: GithubWsImpl

    @Mock
    private lateinit var githubConfig: GithubConfig

    @InjectMocks
    private lateinit var authenticationService: AuthenticationService

    @Before
    fun setup() {
        given(githubConfig.loginBase).willReturn("http://github.com/")
        given(githubConfig.loginAuthorize).willReturn("redirect/{environment}?state={state}&scope={scope}")
    }

    @Test
    fun `should return github url`() {
        /* Given */
        val stateId = "id"
        given(githubLoginStateRepository.save<GithubLoginState>(any())).willReturn(GithubLoginState(stateId))

        /* When */
        val githubRedirectUrl = authenticationService.buildGithubAuthorizeUrl(GithubRedirectEnvironment.PRODUCTION)

        /* Then */
        assertThat(githubRedirectUrl).isEqualTo("http://github.com/redirect/PRODUCTION?state=id&scope=REPO,USER")
    }

    @Test
    fun `should create and store new github state`() {
        /* Given */
        val id = "id"
        given(githubLoginStateRepository.save<GithubLoginState>(any())).willReturn(GithubLoginState(id))

        /* When */
        val stateId = authenticationService.createAndStoreNewGithubState()

        /* Then */
        assertThat(stateId).isEqualTo(id)
    }

    @Test
    fun `should return null when state doesn't exist in database`() {
        /* Given */
        val code = "code"
        val state = "state"
        given(githubLoginStateRepository.existsById(state)).willReturn(false)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        assertThat(accessTokenResult).isEqualTo(null)
        verify(githubLoginStateRepository).existsById(state)
    }

    @Test
    fun `should authenticate on github and store new customer and return access token`() {
        /* Given */
        val code = "code"
        val state = "state"
        val accessToken = "accessToken"
        val githubCustomer = MockData.githubCustomerDto("1")
        val githubCustomerPrimaryEmail = "primaryEmail1"
        val customer = MockData.customer("1")

        given(githubLoginStateRepository.existsById(state)).willReturn(true)
        given(githubWs.getAccessToken(code, state)).willReturn(accessToken)
        given(githubWs.getCustomer(accessToken)).willReturn(githubCustomer)
        given(customerService.existsByGithubId(githubCustomer.id)).willReturn(false)
        given(githubWs.getCustomerPrimaryEmail(accessToken)).willReturn(githubCustomerPrimaryEmail)
        given(customerService.findCustomerByGithubId(githubCustomer.id)).willReturn(customer)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        verify(githubLoginStateRepository).existsById(state)
        verify(githubLoginStateRepository).deleteById(any())
        verify(customerService).createCustomer(any())
        verify(authCredentialsRepository).findAllByCustomerId(any())
        verify(authCredentialsRepository).save<AuthCredentials>(any())
        assertThat(accessTokenResult).isEqualTo(accessToken)
    }

    @Test
    fun `should authenticate on github and return access token`() {
        /* Given */
        val code = "code"
        val state = "state"
        val accessToken = "accessToken"
        val githubCustomer = MockData.githubCustomerDto("2")
        val customer = MockData.customer("2")

        given(githubLoginStateRepository.existsById(any())).willReturn(true)
        given(githubWs.getAccessToken(code, state)).willReturn(accessToken)
        given(githubWs.getCustomer(accessToken)).willReturn(githubCustomer)
        given(customerService.existsByGithubId(githubCustomer.id)).willReturn(true)
        given(customerService.findCustomerByGithubId(githubCustomer.id)).willReturn(customer)

        /* When */
        val accessTokenResult = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        /* Then */
        verify(githubLoginStateRepository).deleteById(any())
        verify(authCredentialsRepository).findAllByCustomerId(any())
        verify(authCredentialsRepository).save<AuthCredentials>(any())
        assertThat(accessTokenResult).isEqualTo(accessToken)
    }

    @Test
    fun `should create and store new customer`() {
        /* Given */
        val githubCustomer = MockData.githubCustomerDto("3")
        val githubCustomerPrimaryEmail = "primaryEmail2"

        /* When */
        authenticationService.createAndStoreNewCustomer(githubCustomer, githubCustomerPrimaryEmail)

        /* Then */
        verify(customerService).createCustomer(check {
            assertThat(it.name).isEqualTo(githubCustomer.name)
            assertThat(it.email).isEqualTo(githubCustomerPrimaryEmail)
            assertThat(it.githubUsername).isEqualTo(githubCustomer.login)
            assertThat(it.githubId).isEqualTo(githubCustomer.id)
        })
    }

    @Test
    fun `should set all access tokens to latest false`() {
        /* Given */
        val authCredentials1 = MockData.authCredentials("1")
        val authCredentials2 = MockData.authCredentials("1").apply { latest = true }
        val listOfAuthCredentials = listOf(authCredentials1, authCredentials2)
        given(authCredentialsRepository.findAllByCustomerId(authCredentials2.customerId)).willReturn(listOfAuthCredentials)

        /* When */
        authenticationService.setAllExistingAccessTokensToLatestFalse(authCredentials2.customerId)

        /* Then */
        assertThat(listOfAuthCredentials[1].latest).isEqualTo(false)
    }

    @Test
    fun `should create and store new auth credentials`() {
        /* Given */
        val accessToken = "accessToken"
        val customer = MockData.customer("3")

        /* When */
        authenticationService.createAndStoreNewAuthCredentials(customer.id!!, accessToken)

        /* Then */
        verify(authCredentialsRepository).save<AuthCredentials>(check {
            assertThat(it.githubAccessToken).isEqualTo(accessToken)
            assertThat(it.customerId).isEqualTo(customer.id)
            assertThat(it.latest).isEqualTo(true)
        })
    }
}