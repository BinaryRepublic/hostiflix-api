package com.hostiflix.support

import com.hostiflix.dto.githubDto.GithubCustomerDto
import com.hostiflix.dto.githubDto.GithubEmailResponseDto
import com.hostiflix.entity.*
import java.time.Instant

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

    fun project(testId : String, customerId: String = "customerId_$testId") : Project {
        return Project(
            testId,
            customerId,
            "hash_$testId",
            "name_$testId",
            "repositoryOwner_$testId",
            "repositoryName_$testId",
            ProjectType.NODEJS.toString(),
            "startCode_$testId",
            "buildCode_$testId",
            Instant.parse("2019-04-01T00:00:00Z"),
            emptyList()
        ).apply {
            branches = listOf(
                branch("1", this),
                branch("2", this)
            )
        }
    }

    fun branch(testId : String, projectTest: Project) : Branch {
        return Branch(
                testId,
            "name_$testId",
            "subdomain_$testId"
        ).apply {
            project = projectTest
            jobs = listOf(
                job("j1", this),
                job("j2", this)
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
}