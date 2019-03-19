package com.hostiflix.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("github")
class GithubConfig {

    lateinit var loginBase : String

    lateinit var loginRedirect : String

    lateinit var loginGetAccessToken : String
}