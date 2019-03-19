package com.hostiflix.webservices

import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubEmailResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GithubWs {

    @Value("\${github.api.base}")
    lateinit var githubApiBase : String

    @Value("\${github.api.user}")
    lateinit var githubApiUser : String

    @Value("\${github.api.emails}")
    lateinit var githubApiEmails : String

    private val restTemplate = RestTemplate()

    fun getCustomer(accessToken: String): GithubCustomerDto {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)

        val response = restTemplate.exchange(
                githubApiBase + githubApiUser,
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
                githubApiBase + githubApiEmails,
                HttpMethod.GET,
                HttpEntity<Any>(headers),
                object : ParameterizedTypeReference<List<GithubEmailResponseDto>>() {}
        )

        return customerEmails.body!!.first { it.primary }.email
    }
}