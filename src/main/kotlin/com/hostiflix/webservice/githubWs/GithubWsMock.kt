package com.hostiflix.webservice.githubWs

import com.hostiflix.dto.GithubBranchDto
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.dto.GithubEmailResponseDto
import com.hostiflix.dto.GithubRepoDto
import com.hostiflix.entity.Project
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class GithubWsMock : GithubWs {
    override fun getAccessToken(code: String, state: String): String {
        return "accessToken"
    }

    override fun getCustomer(accessToken: String): GithubCustomerDto {
        return GithubCustomerDto("id", "name", "login")
    }

    override fun getCustomerPrimaryEmail(accessToken: String): String {
        val githubEmail = GithubEmailResponseDto().apply {
            email = "email"
        }
        return githubEmail.email
    }

    override fun createWebhook(accessToken: String, project: Project) {

    }

    override fun getAllRepos(accessToken: String) : List<GithubRepoDto> {
        return listOf(GithubRepoDto().apply {
            id = "id"
            fullName = "fullName"
            defaultBranch = "defaultBranch"
        })
    }

    override fun getAllBranches(accessToken: String, repoOwner: String, repoName: String) : List<GithubBranchDto> {
        return listOf(GithubBranchDto().apply {
            name = "name"
        })
    }
}