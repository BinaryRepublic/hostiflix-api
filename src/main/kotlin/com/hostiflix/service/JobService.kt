package com.hostiflix.service

import com.hostiflix.entity.Job
import com.hostiflix.entity.JobStatus
import com.hostiflix.repository.JobRepository
import org.springframework.stereotype.Service

@Service
class JobService (
    private val jobRepository: JobRepository
) {
    fun updateJobStatusById(id: String, status: JobStatus): Job? {
        val job = jobRepository.findById(id).takeIf { it.isPresent }?.get() ?: return null
        job.status = status
        return jobRepository.save(job)
    }
}