package com.hostiflix.dto

import org.springframework.beans.factory.annotation.Value

class GithubWebhookRequestConfigDto {

    @Value("\${webhookPayloadUrl}")
    private lateinit var webhookPayloadUrl: String

    val url = webhookPayloadUrl
    val content_type = "json"
}