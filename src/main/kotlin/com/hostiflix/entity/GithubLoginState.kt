package com.hostiflix.entity

import java.io.Serializable
import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash
class GithubLoginState (val id: String = UUID.randomUUID().toString()) : Serializable