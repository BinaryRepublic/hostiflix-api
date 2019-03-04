package com.hostiflix.services

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubEmailResponseDto
import com.hostiflix.entity.Customer
import com.hostiflix.entity.State
import com.hostiflix.entity.Authentication
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.StateRepository
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

@Service
class AuthenticationService (
        private val githubConfig: GithubConfig,
        private val customerService: CustomerService,
        private val stateRepository: StateRepository,
        private val authenticationRepository: AuthenticationRepository
){

    val restTemplate = RestTemplate()

    val redirectUrlDefault = "http://localhost:8080/redirect"
    var initialState : String? = null

    fun isAuthenticated(accessToken: String): Boolean {
        return authenticationRepository.existsById(accessToken)
    }

    fun getRedirectUrlGithub() : String {

        val scope = "repo, user"

        initialState = UUID.randomUUID().toString()
        val newState = State(initialState.toString())

        stateRepository.save(newState)

        return "https://github.com/login/oauth/authorize?client_id=${githubConfig.clientId}&redirect_uri=$redirectUrlDefault&state=$initialState&scope=$scope"

    }

    fun getRedirectUrlHostiflix(code : String, state : String) : String {


        return if (stateRepository.existsById(state)) {

            stateRepository.deleteById(state)


            val url = URI.create("https://github.com/login/oauth/access_token?client_id=${githubConfig.clientId}&client_secret=${githubConfig.clientSecret}&code=$code&redirect_uri=$redirectUrlDefault&state=$state")

            val response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null,
                object : ParameterizedTypeReference<Map<String, String>>() {}
            )

            var accessToken = response.body!!["access_token"]


            val headers = HttpHeaders()
            headers.add("Authorization", "token ${accessToken!!}")
            val request = HttpEntity<String>(headers)

            val customer = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                Map::class.java
            )

            val githubId = customer.body!!["id"].toString()


            if (!customerService.checkGithubId(githubId)) {

                val fullName = customer.body!!["name"].toString().split(" ").toTypedArray()
                val firstName = fullName[0]
                val lastName = fullName[1]
                val githubUsername = customer.body!!["login"].toString()

                val customerEmail = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        request,
                        object : ParameterizedTypeReference<List<GithubEmailResponseDto>>() {}
                )

                // Make sure that only the primary email address from the github account is saved

                var email = customerEmail.body!!.first { it.primary }.email

                val newCustomer = Customer("", firstName, lastName, email, githubUsername, githubId)

                customerService.createCustomer(newCustomer)

            }

            val listOfAuthentication = authenticationRepository.findAll()

            listOfAuthentication.forEach { it.latest = false}

            val customerWithId = customerService.findCustomerByGithubId(githubId)

            val newAuthentication = Authentication(accessToken, customerWithId.id,  true)

            authenticationRepository.save(newAuthentication)


            "http://localhost:8080/projects?access_token=$accessToken"

        } else redirectUrlDefault
    }
}
