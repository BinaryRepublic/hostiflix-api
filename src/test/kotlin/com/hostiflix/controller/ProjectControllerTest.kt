package com.hostiflix.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.Project
import com.hostiflix.service.ProjectService
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JacksonAutoConfiguration::class])
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
    fun `should return a list of all projects`() {
        /* Given */
        val project1 = MockData.project("1")
        val project2 = MockData.project("2")
        val projectList = listOf(project1, project2)
        given(projectService.findAllProjects()).willReturn(projectList)

        /* When, Then */
        mockMvc
            .perform(get("/projects"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projects", hasSize<Project>(2)))
            .andExpect(jsonPath("$.projects[0].id", `is`(project1.id)))
            .andExpect(jsonPath("$.projects[0].customerId", `is`(project1.customerId)))
            .andExpect(jsonPath("$.projects[0].name", `is`(project1.name)))
            .andExpect(jsonPath("$.projects[0].repository", `is`(project1.repository)))
            .andExpect(jsonPath("$.projects[0].projectType", `is`(project1.projectType)))
            .andExpect(jsonPath("$.projects[0].branches[0].id", `is`(project1.branches[0].id)))
            .andExpect(jsonPath("$.projects[0].branches[0].name", `is`(project1.branches[0].name)))
            .andExpect(jsonPath("$.projects[0].branches[1].id", `is`(project1.branches[1].id)))
            .andExpect(jsonPath("$.projects[0].branches[1].name", `is`(project1.branches[1].name)))
            .andExpect(jsonPath("$.projects[1].id", `is`(project2.id)))
            .andExpect(jsonPath("$.projects[1].customerId", `is`(project2.customerId)))
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
        given(projectService.findProjectById(project.id!!)).willReturn(project)

        /* When, Then */
        mockMvc
            .perform(get("/projects/${project.id}"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(project.id)))
            .andExpect(jsonPath("$.customerId", `is`(project.customerId)))
            .andExpect(jsonPath("$.name", `is`(project.name)))
            .andExpect(jsonPath("$.repository", `is`(project.repository)))
            .andExpect(jsonPath("$.projectType", `is`(project.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(project.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(project.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(project.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(project.branches[1].name)))
    }

    @Test
    fun `should return error 400 when no project with given Id is found (findById)`() {
        /* Given */
        val project = MockData.project("4")
        given(projectService.findProjectById(project.id!!)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(get("/projects/${project.id}"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Project ID not found")))
    }

    @Test
    fun `should create and return new project`() {
        /* Given */
        val newProject = MockData.project("5")
        given(projectService.createProject(any())).willReturn(newProject)
        val body = objectMapper.writeValueAsString(newProject)

        /* When, Then */
        mockMvc
            .perform(
                post("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id", `is`(newProject.id)))
            .andExpect(jsonPath("$.customerId", `is`(newProject.customerId)))
            .andExpect(jsonPath("$.name", `is`(newProject.name)))
            .andExpect(jsonPath("$.repository", `is`(newProject.repository)))
            .andExpect(jsonPath("$.projectType", `is`(newProject.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(newProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(newProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(newProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[1].name)))
        verify(projectService).assignProjectToAllBranches(any())
    }

    @Test
    fun `should return updated project`() {
        /* Given */
        val initialProject = MockData.project("6")
        val newProject = MockData.project("6").apply {
            name = "updated"
        }
        given(projectService.existsById(newProject.id!!)).willReturn(true)
        val body = objectMapper.writeValueAsString(newProject)

        /* When, Then */
        mockMvc
            .perform(
                put("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(initialProject.id)))
            .andExpect(jsonPath("$.customerId", `is`(newProject.customerId)))
            .andExpect(jsonPath("$.name", `is`(newProject.name)))
            .andExpect(jsonPath("$.repository", `is`(newProject.repository)))
            .andExpect(jsonPath("$.projectType", `is`(newProject.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(newProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(newProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(newProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[1].name)))
    }

    @Test
    fun `should return error 400 when no project with given Id is found (update)`() {
        /* Given */
        val project = MockData.project("7")
        given(projectService.existsById(project.id!!)).willReturn(false)
        val body = objectMapper.writeValueAsString(project)

        /* When, Then */
        mockMvc
            .perform(
                put("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Project ID not found")))
    }

    @Test
    fun `should return error 204 no content`() {
        /* When, Then */
        mockMvc
            .perform(
                delete("/projects/randomId"))
            .andDo(print())
            .andExpect(status().isNoContent)
    }
}