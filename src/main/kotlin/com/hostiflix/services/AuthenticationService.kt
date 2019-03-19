package com.hostiflix.services

import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.entity.Customer
import com.hostiflix.entity.GithubApplicationScope
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.GithubLoginStateRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import com.hostiflix.webservices.GithubWs

@Service
class AuthenticationService (
        private val authenticationRepository: AuthenticationRepository,
        private val githubLoginStateRepository: GithubLoginStateRepository,
        private val customerService: CustomerService,
        private val githubWs: GithubWs
){
    val restTemplate = RestTemplate()

    @Value("\${github.login.base}")
    lateinit var githubLoginBase : String

    @Value("\${github.login.redirect}")
    lateinit var githubLoginRedirect : String

    @Value("\${github.login.getAccessToken}")
    lateinit var githubLoginGetAccessToken : String

    fun buildNewRedirectUrlForGithub() : String {

        /* No Logic - mock necessary */
        val githubRedirectUrl = githubLoginBase + githubLoginRedirect

        /* No Logic - mock necessary */
        val state = createAndStoreNewGithubState()
        /* No Logic - mock necessary */
        val scope = listOf(GithubApplicationScope.REPO, GithubApplicationScope.USER)

        return githubRedirectUrl
            .replace("{state}", state)
            .replace("{scope}", scope.joinToString(","))
    }

    fun createAndStoreNewGithubState() : String {
        val newState = GithubLoginState()

        return githubLoginStateRepository.save(newState).id
    }

    fun authenticateOnGithubAndReturnAccessToken(code : String, state : String) : String? {
        if (!githubLoginStateRepository.existsById(state)) {
            return null
        }

        githubLoginStateRepository.deleteById(state)

        val accessToken = getAccessTokenFromGithub(code, state)
        val githubCustomer = githubWs.getCustomer(accessToken)

        if (!customerService.existsByGithubId(githubCustomer.id)) {
            var githubCustomerPrimaryEmail = githubWs.getCustomerPrimaryEmail(accessToken)
            createAndStoreNewCustomer(githubCustomer, githubCustomerPrimaryEmail)
        }

        setAllExistingAccessTokensToLatestFalse()
        createAndStoreNewAuthCredentials(githubCustomer.id, accessToken)

        return accessToken
    }

    fun getAccessTokenFromGithub(code: String, state: String) : String {

        val url = githubLoginBase + githubLoginGetAccessToken
            .replace("{code}", code)
            .replace("{state}", state)

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            null,
            object : ParameterizedTypeReference<Map<String, String>>() {}
        )

        return response.body!!.getValue("access_token")
    }

    fun createAndStoreNewCustomer(customer : GithubCustomerDto, primaryEmail : String) {
        val newCustomer = Customer(customer.name, primaryEmail, customer.login, customer.id)

        customerService.createCustomer(newCustomer)
    }

    fun setAllExistingAccessTokensToLatestFalse() {
        val listOfAuthCredentials = authenticationRepository.findAll()
        listOfAuthCredentials.forEach { it.latest = false}
    }

    fun createAndStoreNewAuthCredentials(githubId: String, accessToken: String?) {
        val customer = customerService.findCustomerByGithubId(githubId)
        val newAuthCredentials = AuthCredentials(accessToken!!, customer.id,  true)

        authenticationRepository.save(newAuthCredentials)
    }

    fun isAuthenticated(accessToken: String): Boolean {
        return authenticationRepository.existsByGithubAccessToken(accessToken)
    }
}