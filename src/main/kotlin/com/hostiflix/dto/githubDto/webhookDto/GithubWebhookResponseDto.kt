package com.hostiflix.dto.githubDto.webhookDto

class GithubWebhookResponseDto {
    lateinit var ref : String
    val repository = GithubWebhookResponseRepoDto()
}