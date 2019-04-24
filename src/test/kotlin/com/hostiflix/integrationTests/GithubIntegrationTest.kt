package com.hostiflix.integrationTests

import com.hostiflix.dto.GithubBranchDto
import com.hostiflix.dto.GithubRepoDto
import com.hostiflix.entity.Branch
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class GithubIntegrationTest: BaseIntegrationTest() {

    @Before
    fun setUp() {
        RestAssured.basePath = "/github"
    }

    @Test
    fun `should return http status accepted `() {
        // given
        val branch = Branch("", "master", "subDomain")
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto("refs/heads/${branch.name}")
        val project = projectRepository.save(
            MockData.project("1").apply {
                val project = this
                repositoryOwner = githubWebhookResponseDto.repository.owner.login
                repositoryName = githubWebhookResponseDto.repository.name
                customerId = testCustomer!!.id
                branches = listOf(branch.apply { this.project = project })
            }
        )
        val body = objectMapper.writeValueAsString(githubWebhookResponseDto)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .post("/webhook")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.ACCEPTED.value())

        val resultingProject = projectRepository.findById(project.id!!).get()
        assertThat(resultingProject.branches.first().jobs.size).isEqualTo(1)
    }

    @Test
    fun `should return http status bad request because deployment should not be triggered`() {
        // given
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto()
        projectRepository.save(
            MockData.project("1").apply {
                repositoryOwner = githubWebhookResponseDto.repository.owner.login
                repositoryName = githubWebhookResponseDto.repository.name
                customerId = testCustomer!!.id
            }
        )
        val body = objectMapper.writeValueAsString(githubWebhookResponseDto)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .post("/webhook")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `should return a list of all repos of a customer`() {
        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/repos")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("repos", hasSize<GithubRepoDto>(1))
    }

    @Test
    fun `should return a list of all branches of a repo`() {
        val repoOwner = "repoOwner"
        val repoName = "repoName"

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/repos/$repoOwner/$repoName/branches")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("branches", hasSize<GithubBranchDto>(1))
    }
}