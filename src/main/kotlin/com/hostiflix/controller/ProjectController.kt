package com.hostiflix.controller

import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.services.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(
        private val projectRepository: ProjectRepository,
        private val projectService: ProjectService
) {

    @GetMapping
    fun findAll(): ResponseEntity<*> {

        val projects = projectRepository.findAll()

        return ResponseEntity.ok().body(hashMapOf("projects" to projects))
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

        projectService.assignProjectToAllBranches(newProject)
        projectRepository.save(newProject)

        return ResponseEntity.status(201).body(newProject)
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