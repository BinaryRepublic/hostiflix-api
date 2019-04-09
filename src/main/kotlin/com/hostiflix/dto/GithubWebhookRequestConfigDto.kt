package com.hostiflix.dto

import org.springframework.beans.factory.annotation.Value

class GithubWebhookRequestConfigDto {

    @Value("\${hostiflix-github-webhook}")
    private lateinit var url: String

    val content_type = "json"
}