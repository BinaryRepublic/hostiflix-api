package com.hostiflix.controller

import com.hostiflix.services.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/auth")
class AuthenticationController (
    private val authenticationService: AuthenticationService
){

    @GetMapping("/login")
    fun redirectToGithub(): RedirectView {
        val redirectUrlGithub = authenticationService.buildRedirectUrlGithub()

        return RedirectView(redirectUrlGithub)
    }

    @GetMapping("/redirect")
    fun getAccessToken(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): ResponseEntity<*> {
        val accessToken = authenticationService.getAccessToken(code, state)

        return if (accessToken !== null){
            ResponseEntity.ok().body(accessToken)
        } else {
            ResponseEntity.badRequest().body("states don't match")
        }

    }

}