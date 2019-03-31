package com.hostiflix.webservice.githubWs

import com.hostiflix.dto.GithubCustomerDto

interface GithubWs {

    fun getAccessToken(code: String, state: String) : String

    fun getCustomer(accessToken: String): GithubCustomerDto

    fun getCustomerPrimaryEmail(accessToken: String) : String
}