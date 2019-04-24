package com.hostiflix.service

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.dto.GithubWebhookResponseDto
import com.hostiflix.entity.Job
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.webservice.deploymentWs.DeploymentWs
import com.hostiflix.webservice.githubWs.GithubWs
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class GithubService (
    private val projectRepository: ProjectRepository,
    private val authenticationService: AuthenticationService,
    private val deploymentWs: DeploymentWs,
    private val githubWs: GithubWs
) {
    fun filterWebHooksAndTriggerDeployment(githubWebhookResponseDto: GithubWebhookResponseDto) : HttpStatus {

        val project = projectRepository.findByRepositoryOwnerAndRepositoryName(githubWebhookResponseDto.repository.owner.login, githubWebhookResponseDto.repository.name)!!

        var branches = if (githubWebhookResponseDto.ref != null) {
            val webhookBranch = githubWebhookResponseDto.ref.removePrefix("refs/heads/")
            webhookBranch.let { webhookBranch -> project.branches.filter { it.name == webhookBranch } }
        } else {
            project.branches
        }

        return if (branches.isNotEmpty()){
            val startCode = project.startCode
            val buildCode = project.buildCode
            val token = authenticationService.findAuthCredentialsByCustomerId(project.customerId!!).githubAccessToken
            val gitRepo = githubWebhookResponseDto.repository.html_url.removePrefix("https://")

            for (branch in branches) {
                val subDomain = branch.subDomain
                val deploymentServiceRequestDto = DeploymentServiceRequestDto(startCode, buildCode, token, gitRepo, subDomain)
                val deploymentServiceResponse = deploymentWs.postWebhook(deploymentServiceRequestDto)

                branch.jobs.add(
                    Job(deploymentServiceResponse.id, deploymentServiceResponse.status)
                        .apply { this.branch = branch }
                )
            }

            projectRepository.save(project)

            HttpStatus.ACCEPTED
        } else {
            HttpStatus.BAD_REQUEST
        }
    }

    fun findAllRepos(accessToken : String) = githubWs.getAllRepos(accessToken)

    fun findAllBranches(accessToken: String, repoOwner: String, repoName: String) = githubWs.getAllBranches(accessToken, repoOwner, repoName)
}