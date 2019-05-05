package com.hostiflix.service

import com.hostiflix.entity.Project
import com.hostiflix.entity.ProjectHash
import com.hostiflix.repository.ProjectHashRepository
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.BadRequestException
import com.hostiflix.webservice.githubWs.GithubWs
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class ProjectService (
    private val projectRepository: ProjectRepository,
    private val projectHashRepository: ProjectHashRepository,
    private val githubWs: GithubWs,
    private val authenticationService: AuthenticationService
) {

    fun findAllProjectsByAccessToken(accessToken: String): List<Project> {
        return projectRepository.findAllByCustomerIdOrderByName(getCustomerId(accessToken)).toList()
    }

    fun findProjectByIdAndAccessToken(id: String, accessToken: String): Project? {
        return projectRepository.findByIdAndCustomerId(id, getCustomerId(accessToken))
    }

    fun hasAccessToProject(id: String, accessToken: String): Boolean {
        return projectRepository.existsByIdAndCustomerId(id, getCustomerId(accessToken))
    }

    // INSERT INTO project VALUES (project.id, project.customerId, project.name ...);
    fun saveProject(project: Project, accessToken: String): Project {
        project.customerId = getCustomerId(accessToken)
        return projectRepository.save(project)
    }

    // SELECT * FROM project_hash WHERE id=hash;
    @Transactional
    fun createProject(newProject: Project, accessToken: String) : Project {
        val projectHash = newProject.assignHash?.let { hash ->
            projectHashRepository.findById(hash).takeIf {
                it.isPresent && !projectRepository.existsByProjectHash(it.get())
            }?.get()
        } ?: throw BadRequestException("project hash is missing or invalid")

        newProject.projectHash = projectHash
        val createdProject = saveProject(newProject, accessToken)
        githubWs.createWebhook(accessToken, newProject)
        return createdProject
    }

    // SELECT * FROM project WHERE id=newProject.id);
    fun updateProject(newProject: Project, accessToken: String): Project {
        val currentProject = projectRepository.findById(newProject.id!!).get()
        newProject.branches.forEach { updatedBranch ->
            val currentJobs = currentProject.branches.first { it.id == updatedBranch.id }.jobs
            updatedBranch.jobs = currentJobs
        }
        newProject.projectHash = currentProject.projectHash
        return saveProject(newProject, accessToken)
    }

    // DELETE FROM project WHERE id=id;
    fun deleteProject(id: String) = projectRepository.deleteById(id)

    // SELECT CASE WHEN EXISTS ( SELECT * FROM project_hash WHERE id=randomHash) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    // INSERT INTO project_hash (ProjectHash(randomHash)
    fun createProjectHash(): String {
        var randomHash: String?
        do {
            randomHash = UUID.randomUUID().toString().subSequence(0, 3).toString()
        } while (projectHashRepository.existsById(randomHash!!))
        return projectHashRepository.save(ProjectHash(randomHash)).id
    }

    private fun getCustomerId(accessToken: String): String {
        return authenticationService.getCustomerIdByAccessToken(accessToken)!!
    }
}