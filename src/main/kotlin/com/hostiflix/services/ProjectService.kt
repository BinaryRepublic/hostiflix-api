package com.hostiflix.services

import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import org.springframework.stereotype.Service

@Service
class ProjectService (
    private val projectRepository: ProjectRepository
) {

    fun findAllProjects() : List<Project> = projectRepository.findAll()

    fun findProjectById(id: String): Project? = projectRepository.findById(id).takeIf { it.isPresent }?.get()

    fun assignProjectToAllBranches(newProject: Project) {
        newProject.branches.forEach { it.project = newProject }
    }

    fun createProject(newProject: Project) : Project {
        return projectRepository.save(newProject)
    }

    fun existsById(id: String) = projectRepository.existsById(id)

    fun deleteProject(id: String) = projectRepository.deleteById(id)
}