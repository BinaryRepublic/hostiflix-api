package com.hostiflix.dto

class GithubWebhookResponseRepoDto (
    val name: String,
    val url: String,
    val owner: GithubWebhookReponseRepoOwnerDto
)