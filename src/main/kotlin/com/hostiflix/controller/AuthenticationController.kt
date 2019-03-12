package com.hostiflix.controller

import com.hostiflix.services.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationController (
    private val authenticationService: AuthenticationService
){

    @GetMapping("/login")
    fun redirectToGithub(): ResponseEntity<*> {
        val redirectUrlGithub = authenticationService.buildRedirectUrlGithub()

        return ResponseEntity.ok().body(hashMapOf("redirectUrlGithub" to redirectUrlGithub))
    }

    @GetMapping("/redirect")
    fun manageGithubAuthentication(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): ResponseEntity<*> {
        val accessToken = authenticationService.manageGithubAuthenticationAndReturnAccessToken(code, state)

        return if (accessToken !== null){

            ResponseEntity.ok().body(hashMapOf("accessToken" to accessToken))

        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "states don't match"))
        }

    }

}