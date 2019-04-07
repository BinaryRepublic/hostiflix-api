package com.hostiflix.controller

import com.hostiflix.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/auth")
class AuthenticationController (
    private val authenticationService: AuthenticationService
) {

    @GetMapping("/login")
    fun getGithubAuthorizeUrl(): ResponseEntity<*> {
        val githubAuthorizeUrl = authenticationService.buildGithubAuthorizeUrl()

        return ResponseEntity.ok().body(hashMapOf("githubAuthorizeUrl" to githubAuthorizeUrl))
    }

    @GetMapping("/getRedirectUrl")
    fun getRedirectUrl(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): RedirectView {
        return RedirectView(authenticationService.buildRedirectUrl(code, state))
    }

    @GetMapping("/getAccessToken")
    fun authenticateOnGithubAndReturnAccessToken(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): ResponseEntity<*> {
        val accessToken = authenticationService.authenticateOnGithubAndReturnAccessToken(code, state)

        return if (accessToken != null){
            ResponseEntity.ok().body(hashMapOf("accessToken" to accessToken))
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "states don't match"))
        }
    }
}