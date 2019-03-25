package com.hostiflix

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.controller.ProjectController
import com.hostiflix.entity.Branch
import com.hostiflix.entity.Project
import com.hostiflix.services.ProjectService
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import io.restassured.config.JsonConfig
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
    fun `should return a list of all projects`() {
        /* Given */
        val project1 = Project(
            "customerId1",
            "name1",
            "repository1",
            "projectType1",
            emptyList()
        ).apply { id = "randomString1" }
        val branch1 = Branch(project1, "name1").apply { id ="randomString1" }
        val branch2 = Branch(project1, "name2").apply { id ="randomString2" }
        val listOfBranches1 = listOf(branch1, branch2)
        project1.apply { branches = listOfBranches1 }

        val project2 = Project(
            "customerId2",
            "name2",
            "repository2",
            "projectType2",
            emptyList()
        ).apply { id = "randomString2" }
        val branch3 = Branch(project1, "name3").apply { id ="randomString3" }
        val branch4 = Branch(project1, "name4").apply { id ="randomString4" }
        val listOfBranches2 = listOf(branch3, branch4)
        project2.apply { branches = listOfBranches2 }

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
        val project3 =  Project(
            "customerId3",
            "name3",
            "repository3",
            "projectType3",
            emptyList()
        ).apply { id = "randomString3" }
        val branch3 = Branch(project3, "name3").apply { id ="randomString3" }
        val branch4 = Branch(project3, "name4").apply { id ="randomString4" }
        val listOfBranches3 = listOf(branch3, branch4)
        project3.apply { branches = listOfBranches3 }
        given(projectService.findProjectById(project3.id)).willReturn(project3)

        /* When, Then */
        mockMvc
            .perform(get("/projects/${project3.id}"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(project3.id)))
            .andExpect(jsonPath("$.customerId", `is`(project3.customerId)))
            .andExpect(jsonPath("$.name", `is`(project3.name)))
            .andExpect(jsonPath("$.repository", `is`(project3.repository)))
            .andExpect(jsonPath("$.projectType", `is`(project3.projectType)))
            .andExpect(jsonPath("$.branches[0].id", `is`(project3.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(project3.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(project3.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(project3.branches[1].name)))
    }

    @Test
    fun `should return 400 when no project with given Id is found (findById)`() {
        /* Given */
        val project4 =  Project(
            "customerId4",
            "name4",
            "repository4",
            "projectType4",
            emptyList()
        ).apply { id = "randomString4" }
        given(projectService.findProjectById(project4.id)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(get("/projects/${project4.id}"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Project ID not found")))
    }

    @Test
    fun `should create and return new project`() {
        /* Given */
        val newProject =  Project(
            "customerId8",
            "name8",
            "repository8",
            "projectType8",
            emptyList()
        ).apply { id = "randomString8" }
        val branch10 = Branch(newProject, "name10").apply { id = "randomString10" }
        val branch11 = Branch(newProject, "name11").apply { id = "randomString11" }
        val listOfBranches7 = listOf(branch10, branch11)
        newProject.apply { branches = listOfBranches7 }

        given(projectService.createProject(newProject)).willReturn(newProject)
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
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[0].name)))
            verify(projectService).assignProjectToAllBranches(newProject)
    }

    @Test
    fun `should return updated project`() {
        /* Given */
        val initialProject =  Project(
            "customerId5",
            "name5",
            "repository5",
            "projectType5",
            emptyList()
        ).apply { id = "randomString5" }
        val branch5 = Branch(initialProject, "name5").apply { id = "randomString5" }
        val branch6 = Branch(initialProject, "name6").apply { id = "randomString6" }
        val listOfBranches4 = listOf(branch5, branch6)
        initialProject.apply { branches = listOfBranches4 }

        val newProject =  Project(
            "customerId5",
            "updated",
            "repository5",
            "projectType5",
            emptyList()
        ).apply { id = initialProject.id }
        val branch7 = Branch(newProject, "name7").apply { id = "randomString7" }
        val branch8 = Branch(newProject, "name8").apply { id = "randomString8" }

        val listOfBranches5 = listOf(branch7, branch8)
        newProject.apply { branches = listOfBranches5 }
        given(projectService.existsById(newProject.id)).willReturn(true)
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
                /*
            .andExpect(jsonPath("$.branches[0].id", `is`(newProject.branches[0].id)))
            .andExpect(jsonPath("$.branches[0].name", `is`(newProject.branches[0].name)))
            .andExpect(jsonPath("$.branches[1].id", `is`(newProject.branches[1].id)))
            .andExpect(jsonPath("$.branches[1].name", `is`(newProject.branches[0].name)))
            */
    }

    @Test
    fun `should return 400 when no project with given Id is found (update)`() {
        /* Given */
        val project =  Project(
            "customerId6",
            "name6",
            "repository6",
            "projectType6",
            emptyList()
        ).apply { id = "randomString6" }
        val branch7 = Branch(project, "name5").apply { id = "randomString7" }
        val branch8 = Branch(project, "name6").apply { id = "randomString8" }
        val listOfBranches5 = listOf(branch7, branch8)
        project.apply { branches = listOfBranches5 }
        given(projectService.existsById(project.id)).willReturn(false)
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
    fun `should return 204 no content`() {
        /* When, Then */
        mockMvc
            .perform(
                delete("/projects/randomId"))
            .andDo(print())
            .andExpect(status().isNoContent)
    }
}