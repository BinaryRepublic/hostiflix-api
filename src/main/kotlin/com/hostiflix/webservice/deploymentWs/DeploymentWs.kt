package com.hostiflix.webservice.deploymentWs

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.entity.Job

interface DeploymentWs {

    fun postWebhook(deploymentServiceRequestDto: DeploymentServiceRequestDto) : Job
}