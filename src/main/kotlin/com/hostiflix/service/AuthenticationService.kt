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

    // INSERT INTO github_login_state VALUES (newState.id)
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

    // SELECT CASE WHEN EXISTS (SELECT * FROM github_login_state WHERE id=state) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    // DELETE FROM github_login_state WHERE id=state
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

        val customer = customerService.findCustomerByGithubId(githubCustomer.id)
        setAllExistingAccessTokensToLatestFalse(customer.id!!)
        createAndStoreNewAuthCredentials(customer.id!!, accessToken)

        return accessToken
    }

    fun createAndStoreNewCustomer(customer : GithubCustomerDto, primaryEmail : String) {
        val newCustomer = Customer(null, customer.name, primaryEmail, customer.login, customer.id)

        customerService.createCustomer(newCustomer)
    }

    // UPDATE auth_credentials SET latest=it.latest WHERE id=it.id
    fun setAllExistingAccessTokensToLatestFalse(customerId: String) {
        val listOfAuthCredentials = authCredentialsRepository.findAllByCustomerId(customerId)
        listOfAuthCredentials.forEach {
            it.latest = false
            authCredentialsRepository.save(it)
        }
    }

    // INSERT INTO auth_credentials VALUES (newAuthCredentials.id, newAuthCredentials.githubAccessToken, newAuthCredentials.customerId, newAuthCredentials.latest);
    fun createAndStoreNewAuthCredentials(customerId: String, accessToken: String) {
        val newAuthCredentials = AuthCredentials(null, accessToken, customerId,  true)

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