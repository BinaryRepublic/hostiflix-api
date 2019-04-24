package com.hostiflix.dto

class GithubWebhookResponseRepoDto (
    val name: String,
    val html_url: String,
    val owner: GithubWebhookReponseRepoOwnerDto
)