package com.hostiflix.dto

class GithubWebhookResponseRepoDto {
    lateinit var name: String
    lateinit var url: String
    lateinit var owner: GithubWebhookReponseRepoOwnerDto
}