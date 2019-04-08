package com.hostiflix.controller

import com.hostiflix.dto.GithubRedirectEnvironment
import com.hostiflix.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/auth")
class AuthenticationController (
    private val authenticationService: AuthenticationService
) {

    @GetMapping("/login")
    fun getGithubAuthorizeUrl(
        @RequestParam
        environment: GithubRedirectEnvironment?
    ): ResponseEntity<*> {
        val githubAuthorizeUrl = authenticationService.buildGithubAuthorizeUrl(environment ?: GithubRedirectEnvironment.PRODUCTION)

        return ResponseEntity.ok().body(hashMapOf("githubAuthorizeUrl" to githubAuthorizeUrl))
    }

    @GetMapping("/getRedirectUrl/{environment}")
    fun getRedirectUrl(
        @RequestParam
        code: String,
        @RequestParam
        state: String,
        @PathVariable("environment")
        environment: GithubRedirectEnvironment
    ): RedirectView {
        return RedirectView(authenticationService.buildRedirectUrl(code, state, environment))
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