package com.hostiflix.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubAccessTokenDto (
    @JsonProperty("access_token")
    val accessToken: String
)