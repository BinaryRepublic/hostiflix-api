package com.projectcloud.controller

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

@RestController


class AuthenticationController {

    val clientId = "7e6b741323c2c1392e92"
    val clientSecret = "aae79e7a4b962d9487b52b7e4692811a2ef73755"

    val redirectUri = "http://localhost:8080/redirect"
    val scope = "repo, user"

    var accessToken : String? = null
    var initialState : String? = null

    val restTemplate = RestTemplate()

    @GetMapping("/login")
    fun redirectToGithub(
    ): RedirectView {

        val url = "https://github.com/login/oauth/authorize"
        initialState = UUID.randomUUID().toString()

        return RedirectView("$url?client_id=$clientId&redirect_uri=$redirectUri&state=$initialState&scope=$scope")
    }


    @GetMapping("/redirect")
    fun requestAccesstoken(
            @RequestParam
            code: String,
            @RequestParam
            state: String
    ): RedirectView {

        val returnedState = state

        return if (returnedState == initialState) {

            val url = URI.create("https://github.com/login/oauth/access_token?client_id=$clientId&client_secret=$clientSecret&code=$code&redirect_uri=$redirectUri&state=$returnedState")

            val response: Map<String, String> = restTemplate.postForObject(url, null, Map::class.java) as Map<String, String>
            println(response)

            accessToken = response["access_token"]

            // request customer data from github

            RedirectView("http://localhost:8080/user")

        } else {

            RedirectView("http://localhost:8080/redirect")

        }
    }


    @GetMapping("/user")
    fun fetchData(
    ): ResponseEntity<*> {

        val headers = HttpHeaders()

        headers.add("Authorization", "token ${accessToken!!}")
        println(accessToken)

        val request = HttpEntity<String>(headers)


        return restTemplate.exchange(
            "https://api.github.com/user/repos",
            HttpMethod.GET,
            request,
            object : ParameterizedTypeReference<List<Map<String, Any>>>() {}
        )

        // return ResponseEntity.ok(response.body)
    }
}


// if ::class.java does not work, e.g. object : ParameterizedTypeReference<List<Map<String, Any>>>() {}


/*
val result = restTemplate.exchange(
        "https://api.github.com/user/repos?access_token={accessToken}",
        HttpMethod.GET,
        request,
        object : ParameterizedTypeReference<List<Map<String, Any>>>() {},
        accessToken
)*/


// val response: List<Map<String, Any>> = restTemplate.getForObject(url, List::class.java) as List<Map<String, Any>>
