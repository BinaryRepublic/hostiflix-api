package com.hostiflix.dto

class GithubWebhookResponseDto (
    val ref: String? = null,
    val repository: GithubWebhookResponseRepoDto
)