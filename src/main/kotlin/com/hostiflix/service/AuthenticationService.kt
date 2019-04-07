package com.hostiflix.service

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubRedirectEnvironment
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.Customer
import com.hostiflix.entity.GithubApplicationScope
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.repository.AuthCredentialsRepository
import com.hostiflix.repository.GithubLoginStateRepository
import com.hostiflix.webservice.githubWs.GithubWs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class AuthenticationService (
        private val authCredentialsRepository: AuthCredentialsRepository,
        private val githubLoginStateRepository: GithubLoginStateRepository,
        private val customerService: CustomerService,
        private val githubWs: GithubWs,
        private val githubConfig: GithubConfig
){
    @Value("\${hostiflix-login-redirect.prod}")
    private lateinit var hostiflixLoginRedirectProd: String

    @Value("\${hostiflix-login-redirect.dev}")
    private lateinit var hostiflixLoginRedirectDev: String

    fun buildGithubAuthorizeUrl(environment: GithubRedirectEnvironment) : String {
        val githubAuthorizeUrl = githubConfig.loginBase + githubConfig.loginAuthorize
        val state = createAndStoreNewGithubState()
        val scope = listOf(GithubApplicationScope.REPO, GithubApplicationScope.USER)

        return githubAuthorizeUrl
            .replace("{state}", state)
            .replace("{scope}", scope.joinToString(","))
            .replace("{environment}", environment.toString())
    }

    fun createAndStoreNewGithubState() : String {
        val newState = GithubLoginState()

        return githubLoginStateRepository.save(newState).id
    }

    fun buildRedirectUrl(code: String, state: String, environment: GithubRedirectEnvironment): String {
        val hostiflixLoginRedirect = when(environment) {
            GithubRedirectEnvironment.PRODUCTION -> hostiflixLoginRedirectProd
            GithubRedirectEnvironment.DEVELOPMENT -> hostiflixLoginRedirectDev
        }
        return UriComponentsBuilder
            .fromUriString(hostiflixLoginRedirect)
            .queryParam("code", code)
            .queryParam("state", state)
            .build().toUriString()
    }

    fun authenticateOnGithubAndReturnAccessToken(code : String, state : String) : String? {
        if (!githubLoginStateRepository.existsById(state)) {
            return null
        }

        githubLoginStateRepository.deleteById(state)

        val accessToken = githubWs.getAccessToken(code, state)
        val githubCustomer = githubWs.getCustomer(accessToken)

        if (!customerService.existsByGithubId(githubCustomer.id)) {
            var githubCustomerPrimaryEmail = githubWs.getCustomerPrimaryEmail(accessToken)
            createAndStoreNewCustomer(githubCustomer, githubCustomerPrimaryEmail)
        }

        setAllExistingAccessTokensToLatestFalse()
        createAndStoreNewAuthCredentials(githubCustomer.id, accessToken)

        return accessToken
    }

    fun createAndStoreNewCustomer(customer : GithubCustomerDto, primaryEmail : String) {
        val newCustomer = Customer(null, customer.name, primaryEmail, customer.login, customer.id)

        customerService.createCustomer(newCustomer)
    }

    fun setAllExistingAccessTokensToLatestFalse() {
        val listOfAuthCredentials = authCredentialsRepository.findAll()
        listOfAuthCredentials.forEach {
            it.latest = false
            authCredentialsRepository.save(it)
        }
    }

    fun createAndStoreNewAuthCredentials(githubId: String, accessToken: String?) {
        val customer = customerService.findCustomerByGithubId(githubId)
        val newAuthCredentials = AuthCredentials(null, accessToken!!, customer.id!!,  true)

        authCredentialsRepository.save(newAuthCredentials)
    }

    fun isAuthenticated(accessToken: String): Boolean {
        return authCredentialsRepository.existsByGithubAccessToken(accessToken)
    }

    fun getCustomerIdByAccessToken(accessToken: String): String? {
        return authCredentialsRepository.findByGithubAccessToken(accessToken)?.customerId
    }

    fun findAuthCredentialsByCustomerId(customerId : String) = authCredentialsRepository.findByCustomerIdAndLatest(customerId, true)
}