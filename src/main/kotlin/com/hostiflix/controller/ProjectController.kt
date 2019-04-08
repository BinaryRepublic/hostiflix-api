package com.hostiflix.controller

import com.hostiflix.entity.Project
import com.hostiflix.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @GetMapping
    fun findAll(
        @RequestHeader("Access-Token")
        accessToken: String
    ): ResponseEntity<*> {
        val projectList = projectService.findAllProjectsByAccessToken(accessToken)

        return ResponseEntity.ok().body(hashMapOf("projects" to projectList))
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader("Access-Token")
        accessToken: String,
        @PathVariable
        id: String
    ): ResponseEntity<*> {
        val project = projectService.findProjectByIdAndAccessToken(id, accessToken)

        return if (project != null) {
            ResponseEntity.ok().body(project)
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "invalid project-id or not authorized to access resource"))
        }
    }

    @PostMapping
    fun create(
        @RequestBody
        newProject : Project,
        @RequestHeader("Access-Token")
        accessToken: String
    ): ResponseEntity<*> {
        val createdProject = projectService.createProject(newProject, accessToken)

        return ResponseEntity.status(201).body(createdProject)
    }

    @PutMapping
    fun update(
        @RequestHeader("Access-Token")
        accessToken: String,
        @RequestBody
        newProject: Project
    ): ResponseEntity<*> {
        return if (projectService.hasAccessToProject(newProject.id!!, accessToken)){
            ResponseEntity.ok().body(projectService.updateProject(newProject, accessToken))
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "invalid project-id or not authorized to access resource"))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @RequestHeader("Access-Token")
        accessToken: String,
        @PathVariable
        id: String
    ): ResponseEntity<*> {
        return if (projectService.hasAccessToProject(id, accessToken)){
            projectService.deleteProject(id)
            ResponseEntity.noContent().build<Any>()
        } else {
            ResponseEntity.badRequest().body(hashMapOf("error" to "invalid project-id or not authorized to access resource"))
        }
    }
}