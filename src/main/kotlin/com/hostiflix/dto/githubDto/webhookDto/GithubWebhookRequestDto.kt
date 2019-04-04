package com.hostiflix.dto.githubDto.webhookDto

class GithubWebhookRequestDto {
    val name = "web"
    val active = true
    val events = listOf("push")
    val config = GithubWebhookRequestConfigDto()
}
