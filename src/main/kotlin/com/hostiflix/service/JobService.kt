package com.hostiflix.service

import com.hostiflix.entity.Job
import com.hostiflix.entity.JobStatus
import com.hostiflix.repository.JobRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JobService (
    private val jobRepository: JobRepository
) {
    // SELECT * FROM job WHERE id=id;
    // UPDATE job SET status=job.status WHERE id=job.id
    fun updateJobStatusById(id: String, status: JobStatus): Job? {
        val job = jobRepository.findById(id).takeIf { it.isPresent }?.get() ?: return null
        job.status = status
        if (status == JobStatus.DEPLOYMENT_SUCCESSFUL || status == JobStatus.DEPLOYMENT_FAILED || status == JobStatus.BUILD_FAILED) {
            job.finishedAt = Instant.now()
        }
        return jobRepository.save(job)
    }
}