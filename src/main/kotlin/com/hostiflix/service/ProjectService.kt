package com.hostiflix.service

import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.webservice.githubWs.GithubWs
import org.springframework.stereotype.Service

@Service
class ProjectService (
    private val projectRepository: ProjectRepository,
    private val githubWs: GithubWs,
    private val authenticationService: AuthenticationService
) {

    fun findAllProjectsByAccessToken(accessToken: String): List<Project> {
        return projectRepository.findAllByCustomerId(getCustomerId(accessToken)).toList()
    }

    fun findProjectByIdAndAccessToken(id: String, accessToken: String): Project? {
        return projectRepository.findByIdAndCustomerId(id, getCustomerId(accessToken))
    }

    fun hasAccessToProject(id: String, accessToken: String): Boolean {
        return projectRepository.existsByIdAndCustomerId(id, getCustomerId(accessToken))
    }

    fun saveProject(project: Project, accessToken: String): Project {
        project.customerId = getCustomerId(accessToken)
        return projectRepository.save(project)
    }

    fun createProject(newProject: Project, accessToken: String) : Project {
        githubWs.createWebhook(accessToken, newProject)

        return saveProject(newProject, accessToken)
    }

    fun updateProject(newProject: Project, accessToken: String): Project {
        val currentProject = projectRepository.findById(newProject.id!!).get()
        newProject.branches.forEach { updatedBranch ->
            val currentJobs = currentProject.branches.first { it.id == updatedBranch.id }.jobs
            updatedBranch.jobs = currentJobs
        }
        return saveProject(newProject, accessToken)
    }

    fun deleteProject(id: String) = projectRepository.deleteById(id)

    private fun getCustomerId(accessToken: String): String {
        return authenticationService.getCustomerIdByAccessToken(accessToken)!!
    }
}