package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DeploymentWs(
    private val restTemplate: RestTemplate
) {

    @Value("\${deploymentServiceUrl}")
    lateinit var deploymentServiceUrl : String

    fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : Job {
        val httpEntity = HttpEntity<Any>(deploymentServiceRequestDto)

        val response = restTemplate.exchange(
            deploymentServiceUrl,
            HttpMethod.POST,
            httpEntity,
            Job::class.java
        )

        return response.body!!
    }
}