package com.hostiflix.controller

import com.hostiflix.entity.Customer
import com.hostiflix.services.CustomerService
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

@RestController
class AuthenticationController (
    private val customerService: CustomerService,
    private val githubConfig: GithubConfig
) {

    val redirectUrl = "http://localhost:8080/redirect"

    var initialState : String? = null
    val restTemplate = RestTemplate()



    @GetMapping("/login")
    fun redirectToGithub(): RedirectView {

        val scope = "repo, user"
        initialState = UUID.randomUUID().toString()

        return RedirectView("https://github.com/login/oauth/authorize?client_id=${githubConfig.clientId}&redirect_uri=$redirectUrl&state=$initialState&scope=$scope")

    }


    @GetMapping("/redirect")
    fun getAccessToken(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): RedirectView {

        return if (state == initialState) {

            val url = URI.create("https://github.com/login/oauth/access_token?client_id=${githubConfig.clientId}&client_secret=${githubConfig.clientSecret}&code=$code&redirect_uri=$redirectUrl&state=$state")

            val response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null,
                object : ParameterizedTypeReference<Map<String, String>>() {}
            )

            val accessToken = response.body!!["access_token"]


            val headers = HttpHeaders()
            headers.add("Authorization", "token ${accessToken!!}")
            val request = HttpEntity<String>(headers)

            val customer = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                Map::class.java
            )

            val fullName = customer.body!!["name"].toString().split(" ").toTypedArray()
            val firstName = fullName[0]
            val lastName = fullName[1]
            val githubUsername = customer.body!!["login"].toString()
            val githubId = customer.body!!["id"].toString()


            val customerEmail = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                request,
                object : ParameterizedTypeReference<List<Map<String, Any>>>() {}
            )


            // Make sure that only the primary email address from the github account is saved

            var email = ""

            for (i  in customerEmail.body!!.indices) {
                if (customerEmail.body!![i]["primary"] == true) {
                    email = customerEmail.body!![i]["email"].toString()
                }
            }


            if (!customerService.checkGithubId(githubId)) {

                val newCustomer = Customer("", firstName, lastName, email, githubUsername, githubId, accessToken )

                customerService.createCustomer(newCustomer)

            }

            RedirectView("http://localhost:8080/projects?access_token=$accessToken")

        } else RedirectView(redirectUrl)

    }
}


/*val checkGithubId = restTemplate.exchange(
    "http://localhost:8080/customers/github/$githubId",
    HttpMethod.GET,
    null,
    Customer::class.java
)*/


/*val requestObject = mutableMapOf<String, String>()

    requestObject["firstName"] = firstName
    requestObject["lastName"] = lastName
    requestObject["email"] = email
    requestObject["githubUsername"] = githubUsername
    requestObject["githubId"] = githubId
    requestObject["accessToken"] = accessToken.toString()*/


/*restTemplate.exchange(
    "http://localhost:8080/customers",
    HttpMethod.POST,
    HttpEntity(requestObject),
    Customer::class.java
)*/
