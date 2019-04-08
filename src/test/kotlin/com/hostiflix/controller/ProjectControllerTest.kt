package com.hostiflix.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.config.JsonConfig
import com.hostiflix.entity.Project
import com.hostiflix.service.ProjectService
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders


@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JsonConfig::class])
@WebMvcTest
class ProjectControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jackson2HttpMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var projectService: ProjectService

    @InjectMocks
    lateinit var projectController: ProjectController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(projectController)
            .setMessageConverters(this.jackson2HttpMessageConverter)
            .build()
    }

    @Test
    fun `should return a list of the customers projects`() {
        /* Given */
        val project1 = MockData.project("1")
        val project2 = MockData.project("2")
        val projectList = listOf(project1, project2)
        val accessToken = "accessToken"
        given(projectService.findAllProjectsByAccessToken(accessToken)).willReturn(projectList)

        /* When, Then */
        mockMvc
            .perform(
                get("/projects")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projects", hasSize<Project>(2)))
            .andExpect(jsonPath("$.projects[0].id", `is`(project1.id)))
            .andExpect(jsonPath("$.projects[0].hash", `is`(project1.hash)))
            .andExpect(jsonPath("$.projects[0].name", `is`(project1.name)))
            .andExpect(jsonPath("$.projects[0].repositoryOwner", `is`(project1.repositoryOwner)))
            .andExpect(jsonPath("$.projects[0].repositoryName", `is`(project1.repositoryName)))
            .andExpect(jsonPath("$.projects[0].type", `is`(project1.type)))
            .andExpect(jsonPath("$.projects[0].startCode", `is`(project1.startCode)))
            .andExpect(jsonPath("$.projects[0].buildCode", `is`(project1.buildCode)))
            .andExpect(jsonPath("$.projects[0].createdAt", `is`(project1.createdAt.toString())))
            .andExpect(jsonPath("$.projects[0].branches[0].id", `is`(project1.branches[0].id)))
            .andExpect(jsonPath("$.projects[0].branches[0].name", `is`(project1.branches[0].name)))
            .andExpect(jsonPath("$.projects[0].branches[0].subDomain", `is`(project1.branches[0].subDomain)))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[0].id", `is`(project1.branches[0].jobs[0].id)))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[0].status", `is`(project1.branches[0].jobs[0].status.toString())))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[0].createdAt", `is`(project1.branches[0].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[1].id", `is`(project1.branches[0].jobs[1].id)))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[1].status", `is`(project1.branches[0].jobs[1].status.toString())))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[1].createdAt", `is`(project1.branches[0].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.projects[0].branches[0].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[0].branches[1].id", `is`(project1.branches[1].id)))
            .andExpect(jsonPath("$.projects[0].branches[1].name", `is`(project1.branches[1].name)))
            .andExpect(jsonPath("$.projects[0].branches[1].subDomain", `is`(project1.branches[1].subDomain)))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[0].id", `is`(project1.branches[1].jobs[0].id)))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[0].status", `is`(project1.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[0].createdAt", `is`(project1.branches[1].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[1].id", `is`(project1.branches[1].jobs[1].id)))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[1].status", `is`(project1.branches[1].jobs[1].status.toString())))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[1].createdAt", `is`(project1.branches[1].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[1].id", `is`(project2.id)))
            .andExpect(jsonPath("$.projects[1].hash", `is`(project2.hash)))
            .andExpect(jsonPath("$.projects[1].name", `is`(project2.name)))
            .andExpect(jsonPath("$.projects[1].repositoryOwner", `is`(project2.repositoryOwner)))
            .andExpect(jsonPath("$.projects[1].repositoryName", `is`(project2.repositoryName)))
            .andExpect(jsonPath("$.projects[1].type", `is`(project2.type)))
            .andExpect(jsonPath("$.projects[1].startCode", `is`(project2.startCode)))
            .andExpect(jsonPath("$.projects[1].buildCode", `is`(project2.buildCode)))
            .andExpect(jsonPath("$.projects[1].createdAt", `is`(project2.createdAt.toString())))
            .andExpect(jsonPath("$.projects[1].branches[0].id", `is`(project2.branches[0].id)))
            .andExpect(jsonPath("$.projects[1].branches[0].name", `is`(project2.branches[0].name)))
            .andExpect(jsonPath("$.projects[1].branches[0].subDomain", `is`(project2.branches[0].subDomain)))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[0].id", `is`(project2.branches[0].jobs[0].id)))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[0].status", `is`(project2.branches[0].jobs[0].status.toString())))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[0].createdAt", `is`(project2.branches[0].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[1].id", `is`(project2.branches[0].jobs[1].id)))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[1].status", `is`(project2.branches[0].jobs[1].status.toString())))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[1].createdAt", `is`(project2.branches[0].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.projects[1].branches[0].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[1].branches[1].id", `is`(project2.branches[1].id)))
            .andExpect(jsonPath("$.projects[1].branches[1].name", `is`(project2.branches[1].name)))
            .andExpect(jsonPath("$.projects[1].branches[1].subDomain", `is`(project2.branches[1].subDomain)))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[0].id", `is`(project2.branches[1].jobs[0].id)))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[0].status", `is`(project2.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[0].createdAt", `is`(project2.branches[1].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[1].id", `is`(project2.branches[1].jobs[1].id)))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[1].status", `is`(project2.branches[1].jobs[1].status.toString())))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[1].createdAt", `is`(project2.branches[1].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.projects[1].branches[1].jobs[1].finishedAt").doesNotExist())
    }

    @Test
    fun `should return a project by Id`() {
        /* Given */
        val project = MockData.project("3")
        val accessToken = "accessToken"
        given(projectService.findProjectByIdAndAccessToken(project.id!!, accessToken)).willReturn(project)

        /* When, Then */
        mockMvc
            .perform(
                get("/projects/${project.id}")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(project.id)))
            .andExpect(jsonPath("$.hash", `is`(project.hash)))
            .andExpect(jsonPath("$.name", `is`(project.name)))
            .andExpect(jsonPath("$.repositoryOwner", `is`(project.repositoryOwner)))
            .andExpect(jsonPath("$.repositoryName", `is`(project.repositoryName)))
            .andExpect(jsonPath("$.type", `is`(project.type)))
            .andExpect(jsonPath("$.startCode", `is`(project.startCode)))
            .andExpect(jsonPath("$.buildCode", `is`(project.buildCode)))
            .andExpect(jsonPath("$.createdAt", `is`(project.createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].id", `is`(project.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(project.branches[0].name)))
            .andExpect(jsonPath("$.branches[0].subDomain", `is`(project.branches[0].subDomain)))
            .andExpect(jsonPath("$.branches[0].jobs[0].id", `is`(project.branches[0].jobs[0].id)))
            .andExpect(jsonPath("$.branches[0].jobs[0].status", `is`(project.branches[0].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].createdAt", `is`(project.branches[0].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[0].jobs[1].id", `is`(project.branches[0].jobs[1].id)))
            .andExpect(jsonPath("$.branches[0].jobs[1].status", `is`(project.branches[0].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].createdAt", `is`(project.branches[0].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].id", `is`(project.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(project.branches[1].name)))
            .andExpect(jsonPath("$.branches[1].subDomain", `is`(project.branches[1].subDomain)))
            .andExpect(jsonPath("$.branches[1].jobs[0].id", `is`(project.branches[1].jobs[0].id)))
            .andExpect(jsonPath("$.branches[1].jobs[0].status", `is`(project.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].createdAt", `is`(project.branches[1].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].jobs[1].id", `is`(project.branches[1].jobs[1].id)))
            .andExpect(jsonPath("$.branches[1].jobs[1].status", `is`(project.branches[1].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].createdAt", `is`(project.branches[1].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].finishedAt").doesNotExist())
    }

    @Test
    fun `should return 400 if project id is not existing or user has no access`() {
        /* Given */
        val project = MockData.project("4")
        val accessToken = "accessToken"
        given(projectService.findProjectByIdAndAccessToken(project.id!!, accessToken)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(
                get("/projects/${project.id}")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("invalid project-id or not authorized to access resource")))
    }

    @Test
    fun `should create and return new project`() {
        /* Given */
        val newProject = MockData.project("5")
        val accessToken = "accessToken"
        val body = objectMapper.writeValueAsString(newProject)

        given(projectService.createProject(check {
            assertThat(it.name).isEqualTo(newProject.name)
        }, eq(accessToken))).willReturn(newProject)

        /* When, Then */
        mockMvc
            .perform(
                post("/projects")
                    .header("Access-Token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id", `is`(newProject.id)))
            .andExpect(jsonPath("$.hash", `is`(newProject.hash)))
            .andExpect(jsonPath("$.name", `is`(newProject.name)))
            .andExpect(jsonPath("$.repositoryOwner", `is`(newProject.repositoryOwner)))
            .andExpect(jsonPath("$.repositoryName", `is`(newProject.repositoryName)))
            .andExpect(jsonPath("$.type", `is`(newProject.type)))
            .andExpect(jsonPath("$.startCode", `is`(newProject.startCode)))
            .andExpect(jsonPath("$.buildCode", `is`(newProject.buildCode)))
            .andExpect(jsonPath("$.createdAt", `is`(newProject.createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].id", `is`(newProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(newProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[0].subDomain", `is`(newProject.branches[0].subDomain)))
            .andExpect(jsonPath("$.branches[0].jobs[0].id", `is`(newProject.branches[0].jobs[0].id)))
            .andExpect(jsonPath("$.branches[0].jobs[0].status", `is`(newProject.branches[0].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].createdAt", `is`(newProject.branches[0].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[0].jobs[1].id", `is`(newProject.branches[0].jobs[1].id)))
            .andExpect(jsonPath("$.branches[0].jobs[1].status", `is`(newProject.branches[0].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].createdAt", `is`(newProject.branches[0].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].id", `is`(newProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[1].name)))
            .andExpect(jsonPath("$.branches[1].subDomain", `is`(newProject.branches[1].subDomain)))
            .andExpect(jsonPath("$.branches[1].jobs[0].id", `is`(newProject.branches[1].jobs[0].id)))
            .andExpect(jsonPath("$.branches[1].jobs[0].status", `is`(newProject.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].createdAt", `is`(newProject.branches[1].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].jobs[1].id", `is`(newProject.branches[1].jobs[1].id)))
            .andExpect(jsonPath("$.branches[1].jobs[1].status", `is`(newProject.branches[1].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].createdAt", `is`(newProject.branches[1].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].finishedAt").doesNotExist())
    }

    @Test
    fun `should return updated project`() {
        /* Given */
        val updatedProject = MockData.project("6").apply {
            name = "updated"
        }
        val accessToken = "accessToken"
        val body = objectMapper.writeValueAsString(updatedProject)

        given(projectService.hasAccessToProject(updatedProject.id!!, accessToken)).willReturn(true)
        given(projectService.updateProject(check {
            assertThat(it.name).isEqualTo(updatedProject.name)
        }, eq(accessToken))).willReturn(updatedProject)

        /* When, Then */
        mockMvc
            .perform(
                put("/projects")
                    .header("Access-Token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(updatedProject.id)))
            .andExpect(jsonPath("$.hash", `is`(updatedProject.hash)))
            .andExpect(jsonPath("$.name", `is`(updatedProject.name)))
            .andExpect(jsonPath("$.repositoryOwner", `is`(updatedProject.repositoryOwner)))
            .andExpect(jsonPath("$.repositoryName", `is`(updatedProject.repositoryName)))
            .andExpect(jsonPath("$.type", `is`(updatedProject.type)))
            .andExpect(jsonPath("$.startCode", `is`(updatedProject.startCode)))
            .andExpect(jsonPath("$.buildCode", `is`(updatedProject.buildCode)))
            .andExpect(jsonPath("$.createdAt", `is`(updatedProject.createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].id", `is`(updatedProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(updatedProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[0].subDomain", `is`(updatedProject.branches[0].subDomain)))
            .andExpect(jsonPath("$.branches[0].jobs[0].id", `is`(updatedProject.branches[0].jobs[0].id)))
            .andExpect(jsonPath("$.branches[0].jobs[0].status", `is`(updatedProject.branches[0].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].createdAt", `is`(updatedProject.branches[0].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[0].jobs[1].id", `is`(updatedProject.branches[0].jobs[1].id)))
            .andExpect(jsonPath("$.branches[0].jobs[1].status", `is`(updatedProject.branches[0].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].createdAt", `is`(updatedProject.branches[0].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[0].jobs[1].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].id", `is`(updatedProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(updatedProject.branches[1].name)))
            .andExpect(jsonPath("$.branches[1].subDomain", `is`(updatedProject.branches[1].subDomain)))
            .andExpect(jsonPath("$.branches[1].jobs[0].id", `is`(updatedProject.branches[1].jobs[0].id)))
            .andExpect(jsonPath("$.branches[1].jobs[0].status", `is`(updatedProject.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].createdAt", `is`(updatedProject.branches[1].jobs[0].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[0].finishedAt").doesNotExist())
            .andExpect(jsonPath("$.branches[1].jobs[1].id", `is`(updatedProject.branches[1].jobs[1].id)))
            .andExpect(jsonPath("$.branches[1].jobs[1].status", `is`(updatedProject.branches[1].jobs[1].status.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].createdAt", `is`(updatedProject.branches[1].jobs[1].createdAt.toString())))
            .andExpect(jsonPath("$.branches[1].jobs[1].finishedAt").doesNotExist())
    }

    @Test
    fun `should not update and return 400 if user has no access to project or project is not existing`() {
        /* Given */
        val project = MockData.project("7")
        val accessToken = "accessToken"
        val body = objectMapper.writeValueAsString(project)

        given(projectService.hasAccessToProject(project.id!!, accessToken)).willReturn(false)

        /* When, Then */
        mockMvc
            .perform(
                put("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Access-Token", accessToken)
                    .content(body)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("invalid project-id or not authorized to access resource")))
    }

    @Test
    fun `should delete project and return 204`() {
        /* Given */
        val accessToken = "accessToken"
        val projectId = "p1"
        given(projectService.hasAccessToProject(projectId, accessToken)).willReturn(true)

        /* When, Then */
        mockMvc
            .perform(
                delete("/projects/$projectId")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isNoContent)

        verify(projectService).deleteProject(projectId)
    }

    @Test
    fun `should not delete and return 400 if user has no access to project or project is not existing`() {
        /* Given */
        val accessToken = "accessToken"
        val projectId = "p1"
        given(projectService.hasAccessToProject(projectId, accessToken)).willReturn(false)

        /* When, Then */
        mockMvc
            .perform(
                delete("/projects/$projectId")
                    .header("Access-Token", accessToken)
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("invalid project-id or not authorized to access resource")))

        verify(projectService, never()).deleteProject(projectId)
    }
}