package com.hostiflix.dto.githubDto.webhookDto

class GithubWebhookResponseRepoDto {
    lateinit var name: String
    lateinit var url: String
    lateinit var owner: GithubWebhookReponseRepoOwnerDto
}