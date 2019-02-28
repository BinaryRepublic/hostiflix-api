package com.hostiflix.controller

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("github")
class GithubConfig {

    lateinit var clientSecret: String
    lateinit var clientId: String
}