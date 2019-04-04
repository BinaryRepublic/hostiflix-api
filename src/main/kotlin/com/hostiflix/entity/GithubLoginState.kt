package com.hostiflix.entity

import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.util.*

@RedisHash
class GithubLoginState (val id: String = UUID.randomUUID().toString()) : Serializable