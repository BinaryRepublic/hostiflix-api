package com.hostiflix.controller

import com.hostiflix.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationController (
    private val authenticationService: AuthenticationService
){
    @GetMapping("/login")
    fun buildNewRedirectUrlForGithub(): ResponseEntity<*> {
        val githubRedirectUrl = authenticationService.buildNewRedirectUrlForGithub()

        return ResponseEntity.ok().body(hashMapOf("redirectUrlGithub" to githubRedirectUrl))
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