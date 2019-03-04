package com.hostiflix.controller

import com.hostiflix.services.AuthenticationService
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService
){

    @GetMapping("/login")
    fun redirectToGithub(): RedirectView {
        val redirectUrlGithub = authenticationService.getRedirectUrlGithub()

        return RedirectView(redirectUrlGithub)
    }

    @GetMapping("/redirect")
    fun getAccessToken(
        @RequestParam
        code: String,
        @RequestParam
        state: String
    ): RedirectView {
        val redirectUrlHostiflix = authenticationService.getRedirectUrlHostiflix(code, state)

        return RedirectView(redirectUrlHostiflix)
    }

}











