package com.hostiflix.integrationTests

import com.hostiflix.entity.Project
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class ProjectIntegrationTest: BaseIntegrationTest() {

    @Before
    fun setUp() {
        RestAssured.basePath = "/projects"
    }

    @Test
    fun `should return a list of the customers projects`() {
        val otherCustomer = saveTestCustomerWithAuthCredentials("c2", "accessToken2")
        var p1 = MockData.project("1", testCustomer!!.id!!)
        val p2 = MockData.project("2", otherCustomer.id!!)
        p1 = projectRepository.save(p1)
        projectRepository.save(p2)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get()
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("projects", hasSize<Project>(1))
            .body("projects[0].id", `is`(p1.id))
    }

    @Test
    fun `should return a project by id`() {
        val mockProject = MockData.project("2", testCustomer!!.id!!)
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/${project.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("name", `is`(project.name))
            .body("repository", `is`(project.repository))
            .body("projectType", `is`(project.projectType))
            .body("branches[0].id", isOneOf(project.branches[0].id, project.branches[1].id))
            .body("branches[0].name", isOneOf(project.branches[0].name, project.branches[1].name))
            .body("branches[1].id", isOneOf(project.branches[0].id, project.branches[1].id))
            .body("branches[1].name", isOneOf(project.branches[0].name, project.branches[1].name))
    }

    @Test
    fun `should not return a other customers project by id`() {
        val otherCustomer = saveTestCustomerWithAuthCredentials("c2", "accessToken2")
        val mockProject = MockData.project("2", otherCustomer.id!!)
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .get("/${project.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `should create project`() {
        val newProject = MockData.project("3").apply {
            customerId = null
        }
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
            .body("name", `is`(newProject.name))
            .body("repository", `is`(newProject.repository))
            .body("branches[0].name", `is`(newProject.branches[0].name))
            .body("branches[1].name", `is`(newProject.branches[1].name))
            .body("branches[1].jobs[0].status", `is`(newProject.branches[1].jobs[0].status.toString()))

        val projectList = projectRepository.findAll().toList()
        val project = projectList.first()

        assertThat(projectList.size).isEqualTo(1)
        assertThat(project.branches.size).isEqualTo(2)
    }

    @Test
    fun `should update project`() {
        val mockProject = MockData.project("4", testCustomer!!.id!!)
        project = projectRepository.save(mockProject)
        val updatedProject = project.apply {
            name = "updated"
            customerId = null
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
    fun `should not update other customers project`() {
        val otherCustomer = saveTestCustomerWithAuthCredentials("c2", "accessToken2")
        val mockProject = MockData.project("4", otherCustomer.id!!)
        project = projectRepository.save(mockProject)
        val updatedProject = project.apply {
            name = "updated"
            customerId = null
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
            .statusCode(HttpStatus.BAD_REQUEST.value())

        val projectResult = projectRepository.findById(project.id!!)
        assertThat(projectResult.get().id).isEqualTo(project.id)
        assertThat(projectResult.get().name).isNotEqualTo("updated")
    }

    @Test
    fun `should delete project`() {
        val mockProject = MockData.project("5", testCustomer!!.id!!)
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

    @Test
    fun `should not delete other customers project`() {
        val otherCustomer = saveTestCustomerWithAuthCredentials("c2", "accessToken2")
        val mockProject = MockData.project("5", otherCustomer.id!!)
        project = projectRepository.save(mockProject)

        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .delete("/${project.id}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.BAD_REQUEST.value())

        val projectList = projectRepository.findAll().toList()
        assertThat(projectList.size).isEqualTo(1)
    }
}