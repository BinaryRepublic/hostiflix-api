package com.hostiflix.support

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class EmbeddedRedis {

    private var redisServer: RedisServer? = null

    @Value("\${jedisConFactory.port}")
    lateinit var port : String

    @PostConstruct
    @Throws(IOException::class)
    fun startRedis() {
        redisServer = RedisServer.builder().port(port.toInt()).build()
        redisServer!!.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer!!.stop()
    }
}