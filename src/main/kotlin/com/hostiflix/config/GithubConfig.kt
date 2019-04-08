package com.hostiflix.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("github")
class GithubConfig {
    lateinit var loginBase : String
    lateinit var loginRedirect : String
    lateinit var loginGetAccessToken : String
    lateinit var apiBase : String
    lateinit var apiUser : String
    lateinit var apiEmails : String
    lateinit var apiRepos : String
    lateinit var apiBranches : String
    lateinit var apiWebhook : String
}