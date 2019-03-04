package com.hostiflix.entity

import java.io.Serializable
import org.springframework.data.redis.core.RedisHash

@RedisHash
class State (val id: String) : Serializable