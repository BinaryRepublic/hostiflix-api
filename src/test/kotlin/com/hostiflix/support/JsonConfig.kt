package com.hostiflix.support

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JsonConfig {

    @Bean
    fun jacksonBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder.xml()
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY)
        return builder
    }
}