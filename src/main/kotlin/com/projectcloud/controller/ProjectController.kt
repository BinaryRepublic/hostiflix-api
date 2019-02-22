package com.projectcloud.controller

import com.projectcloud.entity.Project
import com.projectcloud.repository.ProjectRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@RequestMapping("/projects")
class ProjectController(
        private val projectRepository: ProjectRepository
) {

    @PostMapping
    @Transactional
    fun create(
        @RequestBody
        newProject : Project
    ): ResponseEntity<*> {

        newProject.branches.forEach { it.project = newProject }
        projectRepository.save(newProject)

        return  ResponseEntity.status(201).body(newProject)
    }

    @GetMapping("/{customerId}")
    fun findProjectsByCustomerId(
            @PathVariable
            customerId: String
    ): ResponseEntity<*> {

        val projects = projectRepository.findByCustomerId(customerId)

        return ResponseEntity.ok().body(projects)
    }
}