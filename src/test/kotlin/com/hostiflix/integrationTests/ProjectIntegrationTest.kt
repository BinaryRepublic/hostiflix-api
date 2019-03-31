package com.hostiflix.integrationTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.Customer
import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ProjectIntegrationTest {

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${local.server.port}")
    private val serverPort: Int = 0

    lateinit var project : Project
    val accessToken = "accessToken"

    @Before
    fun setUp() {
        RestAssured.port = serverPort
        RestAssured.basePath = "/projects"
    }

    @After
    fun clearDatabase() {
        projectRepository.deleteAll()
    }

    @Test
    fun `should return a list of all projects`() {
        val mockProject = MockData.project("1")
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("projects", hasSize<Customer>(1))
    }

    @Test
    fun `should return a project by id`() {
        val mockProject = MockData.project("2")
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/${project.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("customerId", `is`(project.customerId))
            .body("name", `is`(project.name))
            .body("repository", `is`(project.repository))
            .body("projectType", `is`(project.projectType))
            .body("branches[0].id", `is`(project.branches[0].id))
            .body("branches[0].name", `is`(project.branches[0].name))
            .body("branches[1].id", `is`(project.branches[1].id))
            .body("branches[1].name", `is`(project.branches[1].name))
    }

    @Test
    fun `should return the created project`() {
        val newProject = MockData.project("3")
        val body = objectMapper.writeValueAsString(newProject)

         RestAssured
            .given()
            .header("Access-Token", accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .post()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.CREATED.value())
            .body("customerId", `is`(newProject.customerId))
            .body("name", `is`(newProject.name))
            .body("repository", `is`(newProject.repository))
            .body("branches[0].name", `is`(newProject.branches[0].name))
            .body("branches[1].name", `is`(newProject.branches[1].name))

        val projectList = projectRepository.findAll().toList()
        println(projectList)
        assertThat(projectList.size).isEqualTo(1)
    }

    @Test
    fun `should return updated project`() {
        val mockProject = MockData.project("4")
        project = projectRepository.save(mockProject)
        val updatedProject = project.apply {
            name = "updated"
        }
        val body = objectMapper.writeValueAsString(updatedProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .put()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("id", `is`(project.id))
            .body("customerId", `is`(project.customerId))
            .body("name", `is`("updated"))
            .body("repository", `is`(project.repository))
            .body("branches[0].id", `is`(project.branches[0].id))
            .body("branches[0].name", `is`(project.branches[0].name))
            .body("branches[1].id", `is`(project.branches[1].id))
            .body("branches[1].name", `is`(project.branches[1].name))

        val projectResult = projectRepository.findById(project.id!!)
        assertThat(projectResult.get().id).isEqualTo(project.id)
        assertThat(projectResult.get().name).isEqualTo("updated")
    }

    @Test
    fun `should return no content`() {
        val mockProject = MockData.project("5")
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .delete("/${project.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NO_CONTENT.value())

        val projectList = projectRepository.findAll().toList()
        assertThat(projectList.size).isEqualTo(0)
    }
}