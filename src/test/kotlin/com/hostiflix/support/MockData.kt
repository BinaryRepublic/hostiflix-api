package com.hostiflix.support

import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubEmailResponseDto
import com.hostiflix.entity.AuthCredentials
import com.hostiflix.entity.Branch
import com.hostiflix.entity.Customer
import com.hostiflix.entity.Project

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

    fun project(testId : String) : Project {
        return Project(
            testId,
            "customerId_$testId",
            "name_$testId",
            "repository_$testId",
            "projectType_$testId",
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
            "name_$testId"
        ).apply {
            project = projectTest
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