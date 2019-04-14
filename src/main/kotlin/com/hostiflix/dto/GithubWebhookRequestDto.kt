package com.hostiflix.dto

import com.fasterxml.jackson.annotation.JsonProperty

class GithubWebhookRequestDto (
    webhookEndpoint: String
) {
    val name = "web"

    val active = true

    val events = listOf("push")

    val config = GithubWebhookRequestConfigDto(webhookEndpoint)
}

class GithubWebhookRequestConfigDto (

    val url: String,

    @JsonProperty("content_type")
    val contentType: String = "json"
)