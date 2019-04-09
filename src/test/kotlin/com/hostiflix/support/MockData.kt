package com.hostiflix.support

import com.hostiflix.dto.*
import com.hostiflix.entity.*
import java.time.Instant
import java.util.*

object MockData {

    fun customer(testId : String) : Customer {
        return Customer(
            testId,
            "name_$testId",
            "email_$testId",
            "githubUsername_$testId",
            "githubId_$testId"
        )
    }

    fun project(testId : String, customerId: String = "customerId_$testId", projectHashId: String = testId) : Project {
        return Project(
            testId,
            customerId,
            "name_$testId",
            "repositoryOwner_$testId",
            "repositoryName_$testId",
            ProjectType.NODEJS,
            "startCode_$testId",
            "buildCode_$testId",
            Instant.parse("2019-04-01T00:00:00Z"),
            emptyList()
        ).apply {
            branches = listOf(
                branch("1", this),
                branch("2", this)
            )
            projectHash = ProjectHash(projectHashId)
        }
    }

    fun branch(testId : String, projectTest: Project) : Branch {
        return Branch(
                testId,
            "name_$testId",
            "subDomain_$testId"
        ).apply {
            project = projectTest
            jobs = mutableListOf(
                job(UUID.randomUUID().toString(), this),
                job(UUID.randomUUID().toString(), this)
            )
        }
    }

    fun job(testId: String, branch: Branch): Job {
        return Job(
            testId,
            JobStatus.BUILD_SCHEDULED,
            Instant.parse("2019-04-01T00:00:00Z"),
            null
        ).apply {
            this.branch = branch
        }
    }

    fun githubCustomerDto(testId : String) : GithubCustomerDto {
        return GithubCustomerDto(
            "testId",
            "name_$testId",
            "login_$testId"
        )
    }

    fun githubEmailResponseDto(testId : String) : GithubEmailResponseDto {
        return GithubEmailResponseDto().apply { email = "email_$testId" }
    }

    fun authCredentials(testId : String) : AuthCredentials {
        return AuthCredentials(
            testId,
            "githubAccessToken_$testId",
            "customerId_$testId",
            false
        )
    }

    fun githubWebhookResponseDto() : GithubWebhookResponseDto {
        return GithubWebhookResponseDto().apply {
            ref = "refs/heads/branch"
            repository = githubWebhookResponseRepoDto()
        }
    }

    private fun githubWebhookResponseRepoDto() : GithubWebhookResponseRepoDto {
        return GithubWebhookResponseRepoDto().apply {
            name = "name"
            url = "https://url"
            owner = githubWebhookReposponseRepoOwnerDto()
        }
    }

    private fun githubWebhookReposponseRepoOwnerDto() : GithubWebhookReponseRepoOwnerDto {
        return GithubWebhookReponseRepoOwnerDto().apply {
            name = "name"
        }
    }

    fun githubRepoDto(testId: String): GithubRepoDto {
        return GithubRepoDto().apply {
            id = testId
            fullName = "fullName_$testId"
            defaultBranch = "defaultBranch_$testId"
            owner = githubRepoOwnerDto()
        }
    }

    fun githubRepoOwnerDto(): GithubRepoOwnerDto {
        return GithubRepoOwnerDto().apply {
            login = "login"
        }
    }

    fun githubBranchDto(testId: String) : GithubBranchDto {
        return GithubBranchDto().apply {
            name = "name_$testId"
        }
    }

    fun deploymentServiceRequestDto(testId: String): DeploymentServiceRequestDto {
        return DeploymentServiceRequestDto(
            "startCode_$testId",
            "buildCode_$testId",
            "githubAccessToken_$testId",
            "url",
            "subDomain_$testId"
        )
    }
}