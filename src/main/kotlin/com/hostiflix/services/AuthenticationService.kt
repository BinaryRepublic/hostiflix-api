package com.hostiflix.services

import com.hostiflix.dto.GithubEmailResponseDto
import com.hostiflix.entity.Customer
import com.hostiflix.entity.GithubApplicationScope
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.entity.Authentication
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.CustomerRepository
import com.hostiflix.repository.StateRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthenticationService (
    private val customerService: CustomerService,
    private val stateRepository: StateRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val customerRepository: CustomerRepository
){

    val restTemplate = RestTemplate()

    @Value("\${githubRedirectUrl}")
    lateinit var githubRedirectUrl : String

    @Value("\${githubGetAccessTokenUrl}")
    lateinit var githubGetAccessTokenUrl : String

    fun buildRedirectUrlGithub() : String {

        val state = createAndStoreNewGithubState()

        val scope = listOf(GithubApplicationScope.REPO, GithubApplicationScope.USER)

        return githubRedirectUrl
            .replace("{state}", state)
            .replace("{scope}", scope.joinToString(","))

    }

    fun createAndStoreNewGithubState() : String {

        val newState = GithubLoginState()

        return stateRepository.save(newState).id

    }

    fun manageGithubAuthenticationAndReturnAccessToken(code : String, state : String) : String? {

        return if (stateRepository.existsById(state)) {

            stateRepository.deleteById(state)

            val accessToken = getAccessToken(code, state)

            val customer = getCustomerDataFromGithub(accessToken!!)

            val githubId = customer.body!!["id"].toString()

            if (!customerService.existsByGithubId(githubId)) {

                var customerPrimaryEmail = getCustomerPrimaryEmailFromGithub(accessToken)

                createAndStoreNewCustomer(customer, customerPrimaryEmail)
            }

            setAllExistingAccessTokensToExpired()
            createAndStoreNewAuthentication(githubId, accessToken)

            accessToken

        } else null
    }

    fun getAccessToken(code: String, state: String) : String? {
        val url = githubGetAccessTokenUrl
                .replace("{code}", code)
                .replace("{state}", state)

        val response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null,
                object : ParameterizedTypeReference<Map<String, String>>() {}
        )

        return response.body!!["access_token"]
    }

    fun getCustomerDataFromGithub(accessToken: String?) : ResponseEntity<Map<*, *>> {
        val request = addAccessTokenToHttpHeader(accessToken)

        return restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                Map::class.java
        )
    }

    fun getCustomerPrimaryEmailFromGithub(accessToken: String?) : String {
        val request = addAccessTokenToHttpHeader(accessToken)

        val customerEmails = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<List<GithubEmailResponseDto>>() {}
        )

        return customerEmails.body!!.first { it.primary }.email
    }

    fun addAccessTokenToHttpHeader(accessToken: String?) : HttpEntity<String> {
        val headers = HttpHeaders()
        headers.add("Authorization", "token ${accessToken!!}")

        return HttpEntity(headers)
    }

    fun createAndStoreNewCustomer(customer : ResponseEntity<Map<*, *>>, primaryEmail : String) {
        val name = customer.body!!["name"].toString()
        val githubUsername = customer.body!!["login"].toString()
        val githubId = customer.body!!["id"].toString()

        val newCustomer = Customer("", name, primaryEmail, githubUsername, githubId)

        customerRepository.save(newCustomer)
    }

    fun setAllExistingAccessTokensToExpired() {

        val listOfAuthentication = authenticationRepository.findAll()

        listOfAuthentication.forEach { it.latest = false}

    }

    fun createAndStoreNewAuthentication(githubId: String, accessToken: String?) {

        val customer = customerService.findCustomerByGithubId(githubId)

        val newAuthentication = Authentication("", accessToken!!, customer.id,  true)

        authenticationRepository.save(newAuthentication)

    }

    fun isAuthenticated(accessToken: String): Boolean {
        return authenticationRepository.existsByGithubAccessToken(accessToken)
    }
}
