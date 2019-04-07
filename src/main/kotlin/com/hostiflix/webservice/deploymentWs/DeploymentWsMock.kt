package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.entity.Job
import com.hostiflix.entity.JobStatus
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class DeploymentWsMock : DeploymentWs {
    override fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : Job {
        return Job("id", JobStatus.BUILD_SCHEDULED)
    }
}