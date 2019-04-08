package com.hostiflix.dto

import com.hostiflix.entity.JobStatus

class DeploymentServiceResponseDto (
    val id: String,

    val status: JobStatus
)