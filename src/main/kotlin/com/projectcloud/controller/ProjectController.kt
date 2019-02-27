package com.projectcloud.controller

import com.projectcloud.entity.Project
import com.projectcloud.repository.ProjectRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@RequestMapping("/projects")
class ProjectController(
        private val projectRepository: ProjectRepository
) {

    @GetMapping("/all/{customerId}")
    fun findAllProjectsByCustomerId(
            @PathVariable
            customerId: String
    ): ResponseEntity<*> {

        val projects = projectRepository.findByCustomerId(customerId)

        return ResponseEntity.ok().body(projects)
    }


    @GetMapping("/{id}")
    fun findProjectById(
            @PathVariable
            id: String
    ): ResponseEntity<*> {

        val project = projectRepository.findById(id)

        return ResponseEntity.ok().body(project)
    }


    @PostMapping
    fun create(
        @RequestBody
        newProject : Project
    ): ResponseEntity<*> {

        newProject.branches.forEach { it.project = newProject }
        projectRepository.save(newProject)

        return  ResponseEntity.status(201).body(newProject)
    }


    @PutMapping
    fun update(
        @RequestBody
        newProject: Project
    ): ResponseEntity<*> {

        return if (projectRepository.existsById(newProject.id)){
            projectRepository.save(newProject)
            ResponseEntity.ok().body(newProject)
        } else {
            ResponseEntity<Project>(HttpStatus.BAD_REQUEST)
        }
    }


    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable
        id: String
    ): ResponseEntity<*> {

        projectRepository.deleteById(id)

        return ResponseEntity<Project>(HttpStatus.NO_CONTENT)
    }

}