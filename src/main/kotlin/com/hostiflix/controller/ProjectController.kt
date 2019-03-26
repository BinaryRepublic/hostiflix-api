package com.hostiflix.controller

import com.hostiflix.entity.Project
import com.hostiflix.services.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @GetMapping
    fun findAll(): ResponseEntity<*> {
        val projectList = projectService.findAllProjects()

        return ResponseEntity.ok().body(hashMapOf("projects" to projectList))
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable
        id: String
    ): ResponseEntity<*> {
        val project = projectService.findProjectById(id)

        return if (project != null) {
            ResponseEntity.ok().body(project)
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "Project ID not found"))
        }
    }

    @PostMapping
    fun create(
        @RequestBody
        newProject : Project
    ): ResponseEntity<*> {
        projectService.assignProjectToAllBranches(newProject)
        val createdProject = projectService.createProject(newProject)

        return ResponseEntity.status(201).body(createdProject)
    }

    @PutMapping
    fun update(
        @RequestBody
        newProject: Project
    ): ResponseEntity<*> {
        return if (projectService.existsById(newProject.id!!)){
            projectService.createProject(newProject)
            ResponseEntity.ok().body(newProject)
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "Project ID not found"))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable
        id: String
    ): ResponseEntity<Void> {
        projectService.deleteProject(id)

        return ResponseEntity.noContent().build()
    }
}