package com.hostiflix.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.dto.GithubRepoDto
import com.hostiflix.service.GithubService
import com.hostiflix.support.JsonConfig
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.given
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JsonConfig::class, JacksonAutoConfiguration::class])
@WebMvcTest
class GithubControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var githubService: GithubService

    @InjectMocks
    lateinit var githubController: GithubController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(githubController)
            .build()
    }

    @Test
    fun `should return http status accepted`() {
        /* Given */
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto()
        val body = objectMapper.writeValueAsString(githubWebhookResponseDto)

        given(githubService.filterWebHooksAndTriggerDeployment(check {
            assertThat(it).isEqualToComparingFieldByFieldRecursively(githubWebhookResponseDto)
        })).willReturn(HttpStatus.ACCEPTED)

        /* When, Then */
        mockMvc
            .perform(
                post("/github/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isAccepted)
    }

    @Test
    fun `should return http status bad request because deployment should not be triggered`() {
        /* Given */
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto()
        val body = objectMapper.writeValueAsString(githubWebhookResponseDto)

        given(githubService.filterWebHooksAndTriggerDeployment(check {
            assertThat(it).isEqualToComparingFieldByFieldRecursively(githubWebhookResponseDto)
        })).willReturn(HttpStatus.BAD_REQUEST)

        /* When, Then */
        mockMvc
            .perform(
                post("/github/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return a list of the customers repos`() {
        /* Given */
        val accessToken = "accessToken"
        val repo1 = MockData.githubRepoDto("1")
        val repo2 = MockData.githubRepoDto("2")
        val repoList = listOf(repo1, repo2)
        given(githubService.findAllRepos(accessToken)).willReturn(repoList)

        /* When, Then */
        mockMvc
            .perform(
                get("/github/repos")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos", hasSize<GithubRepoDto>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[0].id", `is`(repo1.id)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[0].fullName", `is`(repo1.fullName)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[0].defaultBranch", `is`(repo1.defaultBranch)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[1].id", `is`(repo2.id)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[1].fullName", `is`(repo2.fullName)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repos[1].defaultBranch", `is`(repo2.defaultBranch)))
    }

    @Test
    fun `should return a list of the repo's branches`() {
        /* Given */
        val accessToken = "accessToken"
        val repoOwner = "repoOwner"
        val repoName = "repoName"
        val branch1 = MockData.githubBranchDto("1")
        val branch2 = MockData.githubBranchDto("2")
        val branchList = listOf(branch1, branch2)
        given(githubService.findAllBranches(accessToken, repoOwner, repoName)).willReturn(branchList)

        /* When, Then */
        mockMvc
            .perform(
                get("/github/repos/$repoOwner/$repoName/branches")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.branches", hasSize<GithubRepoDto>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.branches[0].name", `is`(branch1.name)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.branches[1].name", `is`(branch2.name)))
    }
}