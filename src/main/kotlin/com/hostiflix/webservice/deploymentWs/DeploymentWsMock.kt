package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.dto.DeploymentServiceResponseDto
import com.hostiflix.entity.JobStatus
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class DeploymentWsMock : DeploymentWs {
    override fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : DeploymentServiceResponseDto {
        return DeploymentServiceResponseDto("id", JobStatus.BUILD_SCHEDULED)
    }
}