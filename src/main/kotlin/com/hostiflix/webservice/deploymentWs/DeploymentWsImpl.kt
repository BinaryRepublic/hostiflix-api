package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.entity.Job
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@Profile("!test")
class DeploymentWsImpl(
    private val restTemplate: RestTemplate
): DeploymentWs {

    @Value("\${deploymentServiceUrl}")
    lateinit var deploymentServiceUrl : String

    override fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : Job {
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