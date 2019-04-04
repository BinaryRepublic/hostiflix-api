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
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JsonConfig::class, JacksonAutoConfiguration::class])
@WebMvcTest
class ProjectControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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
            .andExpect(jsonPath("$.projects[0].name", `is`(project1.name)))
            .andExpect(jsonPath("$.projects[0].repository", `is`(project1.repository)))
            .andExpect(jsonPath("$.projects[0].projectType", `is`(project1.projectType)))
            .andExpect(jsonPath("$.projects[0].branches[0].id", `is`(project1.branches[0].id)))
            .andExpect(jsonPath("$.projects[0].branches[0].name", `is`(project1.branches[0].name)))
            .andExpect(jsonPath("$.projects[0].branches[1].id", `is`(project1.branches[1].id)))
            .andExpect(jsonPath("$.projects[0].branches[1].name", `is`(project1.branches[1].name)))
            .andExpect(jsonPath("$.projects[0].branches[1].jobs[0].status", `is`(project1.branches[1].jobs[0].status.toString())))
            .andExpect(jsonPath("$.projects[1].id", `is`(project2.id)))
            .andExpect(jsonPath("$.projects[1].name", `is`(project2.name)))
            .andExpect(jsonPath("$.projects[1].repository", `is`(project2.repository)))
            .andExpect(jsonPath("$.projects[1].projectType", `is`(project2.projectType)))
            .andExpect(jsonPath("$.projects[1].branches[0].id", `is`(project2.branches[0].id)))
            .andExpect(jsonPath("$.projects[1].branches[0].name", `is`(project2.branches[0].name)))
            .andExpect(jsonPath("$.projects[1].branches[1].id", `is`(project2.branches[1].id)))
            .andExpect(jsonPath("$.projects[1].branches[1].name", `is`(project2.branches[1].name)))
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
            .andExpect(jsonPath("$.name", `is`(project.name)))
            .andExpect(jsonPath("$.repository", `is`(project.repository)))
            .andExpect(jsonPath("$.projectType", `is`(project.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(project.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(project.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(project.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(project.branches[1].name)))
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

        given(projectService.saveProject(check {
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
            .andExpect(jsonPath("$.name", `is`(newProject.name)))
            .andExpect(jsonPath("$.repository", `is`(newProject.repository)))
            .andExpect(jsonPath("$.projectType", `is`(newProject.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(newProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(newProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(newProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[1].name)))
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
        given(projectService.saveProject(check {
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
            .andExpect(jsonPath("$.name", `is`(updatedProject.name)))
            .andExpect(jsonPath("$.repository", `is`(updatedProject.repository)))
            .andExpect(jsonPath("$.projectType", `is`(updatedProject.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(updatedProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(updatedProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(updatedProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(updatedProject.branches[1].name)))
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