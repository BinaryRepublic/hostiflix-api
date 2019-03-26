package com.hostiflix.webservices

import com.hostiflix.config.GithubConfig
import com.hostiflix.dto.GithubAccessTokenDto
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubEmailResponseDto
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GithubWs(
    private val githubConfig: GithubConfig,
    private val restTemplate: RestTemplate
) {

    fun getAccessToken(code: String, state: String) : String {
        val url = githubConfig.loginBase + githubConfig.loginGetAccessToken

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            null,
            GithubAccessTokenDto::class.java,
            code,
            state
        )

        return response.body!!.accessToken
    }

    fun getCustomer(accessToken: String): GithubCustomerDto {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)

        val response = restTemplate.exchange(
            githubConfig.apiBase + githubConfig.apiUser,
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            GithubCustomerDto::class.java
        )

        return response.body!!
    }

    fun getCustomerPrimaryEmail(accessToken: String) : String {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)

        val customerEmails = restTemplate.exchange(
            githubConfig.apiBase + githubConfig.apiEmails,
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<List<GithubEmailResponseDto>>() {}
        )

        return customerEmails.body!!.first { it.primary }.email
    }
}