package com.hostiflix.webservice.githubWs

import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubEmailResponseDto
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class GithubWsMock : GithubWs {
    override fun getAccessToken(code: String, state: String): String {
        return "accessToken"
    }

    override fun getCustomer(accessToken: String): GithubCustomerDto {
        return GithubCustomerDto("id", "name", "login")
    }

    override fun getCustomerPrimaryEmail(accessToken: String): String {
        val githubEmail = GithubEmailResponseDto().apply {
            email = "email"
        }
        return githubEmail.email
    }
}