package com.hostiflix.service

import com.hostiflix.dto.DeploymentServiceRequestDto
import com.hostiflix.dto.githubDto.webhookDto.GithubWebhookResponseDto
import com.hostiflix.webservice.deploymentWs.DeploymentWs
import com.hostiflix.webservice.githubWs.GithubWs
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class GithubService (
    private val projectService: ProjectService,
    private val authenticationService: AuthenticationService,
    private val deploymentWs: DeploymentWs,
    private val githubWs: GithubWs
) {
    fun filterWebHooksAndTriggerDeployment(githubWebhookResponseDto: GithubWebhookResponseDto) : HttpStatus {
        val webhookBranch = githubWebhookResponseDto.ref.removePrefix("refs/heads/")
        val project = projectService.findProjectByRepositoryOwnerAndRepositoryName(githubWebhookResponseDto.repository.owner.name, githubWebhookResponseDto.repository.name)!!
        val branch = project.branches.firstOrNull { it.name == webhookBranch }

        return if (branch != null){
            val startCode = project.startCode
            val buildCode = project.buildCode
            val token = authenticationService.findAuthCredentialsByCustomerId(project!!.customerId).githubAccessToken
            val gitRepo = githubWebhookResponseDto.repository.url.removePrefix("https://")
            val subDomain = branch.subDomain

            val deploymentServiceRequestDto = DeploymentServiceRequestDto(startCode, buildCode, token, gitRepo, subDomain)

            val returnedJob = deploymentWs.postWebhook(deploymentServiceRequestDto)

            val job = Job(returnedJob.id, returnedJob.status).apply {
                this.branch = branch
            }
            branch.jobs = listOf(job)
            projectService.saveProject(project)

            HttpStatus.ACCEPTED
        } else {
            HttpStatus.BAD_REQUEST
        }
    }

    fun findAllRepos(accessToken : String) = githubWs.getAllRepos(accessToken)

    fun findAllBranches(accessToken: String, repoOwner: String, repoName: String) = githubWs.getAllBranches(accessToken, repoOwner, repoName)
}