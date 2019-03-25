package com.hostiflix.services

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.entity.Customer
import com.hostiflix.entity.GithubApplicationScope
import com.hostiflix.entity.GithubLoginState
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.repository.AuthenticationRepository
import com.hostiflix.repository.GithubLoginStateRepository
import org.springframework.stereotype.Service
import com.hostiflix.webservices.GithubWs

@Service
class AuthenticationService (
    private val authenticationRepository: AuthenticationRepository,
    private val githubLoginStateRepository: GithubLoginStateRepository,
    private val customerService: CustomerService,
    private val githubWs: GithubWs,
    private val githubConfig: GithubConfig
){
    fun buildNewRedirectUrlForGithub() : String {
        val githubRedirectUrl = githubConfig.loginBase + githubConfig.loginRedirect
        val state = createAndStoreNewGithubState()
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