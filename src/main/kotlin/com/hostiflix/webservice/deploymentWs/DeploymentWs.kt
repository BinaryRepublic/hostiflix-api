package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.dto.DeploymentServiceResponseDto

interface DeploymentWs {

    fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : DeploymentServiceResponseDto
}