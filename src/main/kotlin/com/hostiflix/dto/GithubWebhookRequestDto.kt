package com.hostiflix.dto

class GithubWebhookRequestDto {
    val name = "web"
    val active = true
    val events = listOf("push")
    val config = GithubWebhookRequestConfigDto()
}
