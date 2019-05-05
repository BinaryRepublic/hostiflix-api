package com.hostiflix.controller

import com.hostiflix.entity.JobStatus
import com.hostiflix.service.JobService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobs")
class JobController (
    private val jobService: JobService
) {

    @PutMapping("/{id}/status/{status}")
    fun updateJobStatus(
        @PathVariable
        id: String,
        @PathVariable
        status: JobStatus
    ): ResponseEntity<*> {
        if (jobService.updateJobStatusById(id, status) == null) {
            return ResponseEntity.notFound().build<Any>()
        }
        return ResponseEntity.noContent().build<Any>()
    }
}