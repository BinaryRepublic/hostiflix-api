package com.hostiflix.controller

import com.hostiflix.dto.githubDto.webhookDto.GithubWebhookResponseDto
import com.hostiflix.service.GithubService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/github")
class GithubController(
    private val githubService: GithubService
) {
    @PostMapping("/webhook")
    fun filterWebHooksAndTriggerDeployment(
        @RequestBody
        githubWebhookResponseDto : GithubWebhookResponseDto
    ): HttpStatus {
        return githubService.filterWebHooksAndTriggerDeployment(githubWebhookResponseDto)
    }

    @GetMapping("/repos")
    fun findRepos(
        @RequestHeader
        ("Access-Token") accessToken: String
    ): ResponseEntity<*> {
        val repoList = githubService.findAllRepos(accessToken)

        return ResponseEntity.ok().body(hashMapOf("repos" to repoList))
    }

    @GetMapping("/repos/{repoOwner}/{repoName}/branches")
    fun findBranches(
        @RequestHeader
        ("Access-Token") accessToken: String,
        @PathVariable
        repoOwner : String,
        @PathVariable
        repoName : String
    ): ResponseEntity<*> {
        val branchList = githubService.findAllBranches(accessToken, repoOwner, repoName)

        return ResponseEntity.ok().body(hashMapOf("branches" to branchList))
    }
}