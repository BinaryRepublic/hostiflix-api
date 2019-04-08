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
            .body("hash", `is`(project.hash))
            .body("name", `is`(project.name))
            .body("repositoryOwner", `is`(project.repositoryOwner))
            .body("repositoryName", `is`(project.repositoryName))
            .body("type", `is`(project.type))
            .body("startCode", `is`(project.startCode))
            .body("buildCode", `is`(project.buildCode))
            .body("createdAt", `is`(project.createdAt.toString()))
            .body("branches[0].id", `is`(project.branches[0].id))
            .body("branches[0].name", `is`(project.branches[0].name))
            .body("branches[0].subDomain", `is`(project.branches[0].subDomain))
            .body("branches[0].jobs[0].id", `is`(project.branches[0].jobs[0].id))
            .body("branches[0].jobs[0].status", `is`(project.branches[0].jobs[0].status.toString()))
            .body("branches[0].jobs[0].createdAt", `is`(project.branches[0].jobs[0].createdAt.toString()))
            .body("branches[0].jobs[1].id", `is`(project.branches[0].jobs[1].id))
            .body("branches[0].jobs[1].status", `is`(project.branches[0].jobs[1].status.toString()))
            .body("branches[0].jobs[1].createdAt", `is`(project.branches[0].jobs[1].createdAt.toString()))
            .body("branches[1].id", `is`(project.branches[1].id))
            .body("branches[1].name", `is`(project.branches[1].name))
            .body("branches[1].subDomain", `is`(project.branches[1].subDomain))
            .body("branches[1].jobs[0].id", `is`(project.branches[1].jobs[0].id))
            .body("branches[1].jobs[0].status", `is`(project.branches[1].jobs[0].status.toString()))
            .body("branches[1].jobs[0].createdAt", `is`(project.branches[1].jobs[0].createdAt.toString()))
            .body("branches[1].jobs[1].id", `is`(project.branches[1].jobs[1].id))
            .body("branches[1].jobs[1].status", `is`(project.branches[1].jobs[1].status.toString()))
            .body("branches[1].jobs[1].createdAt", `is`(project.branches[1].jobs[1].createdAt.toString()))
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
    fun `should create project and ignore passed jobs`() {
        val newProject = MockData.project("3").apply {
            customerId = testCustomer!!.id
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
            .body("hash", `is`(newProject.hash))
            .body("name", `is`(newProject.name))
            .body("repositoryOwner", `is`(newProject.repositoryOwner))
            .body("repositoryName", `is`(newProject.repositoryName))
            .body("type", `is`(newProject.type))
            .body("startCode", `is`(newProject.startCode))
            .body("buildCode", `is`(newProject.buildCode))
            .body("branches[0].name", `is`(newProject.branches[0].name))
            .body("branches[0].subDomain", `is`(newProject.branches[0].subDomain))
            .body("branches[0].jobs", nullValue())
            .body("branches[1].name", `is`(newProject.branches[1].name))
            .body("branches[1].subDomain", `is`(newProject.branches[1].subDomain))
            .body("branches[1].jobs", nullValue())

        val projectList = projectRepository.findAll().toList()
        val project = projectList.first()

        assertThat(projectList.size).isEqualTo(1)
        assertThat(project.branches.size).isEqualTo(2)
        assertThat(project.branches.first().jobs).isEmpty()
    }

    @Test
    fun `should update project without touching jobs`() {
        val mockProject = MockData.project("4", testCustomer!!.id!!)
        val project = projectRepository.save(mockProject)
        val updatedProject = project.copy()
        updatedProject.apply {
            name = "updated"
            customerId = null
            branches = branches.map { it.copy().apply { jobs = mutableListOf(MockData.job("12345", it)) } }
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
            .body("id", `is`(updatedProject.id))
            .body("name", `is`("updated"))
            .body("hash", `is`(updatedProject.hash))
            .body("repositoryOwner", `is`(updatedProject.repositoryOwner))
            .body("repositoryName", `is`(updatedProject.repositoryName))
            .body("type", `is`(updatedProject.type))
            .body("startCode", `is`(updatedProject.startCode))
            .body("buildCode", `is`(updatedProject.buildCode))
            .body("createdAt", `is`(updatedProject.createdAt.toString()))
            .body("branches[0].id", `is`(updatedProject.branches[0].id))
            .body("branches[0].name", `is`(updatedProject.branches[0].name))
            .body("branches[0].subDomain", `is`(updatedProject.branches[0].subDomain))
            .body("branches[0].jobs[0].id", `is`(project.branches[0].jobs[0].id))
            .body("branches[0].jobs[0].status", `is`(project.branches[0].jobs[0].status.toString()))
            .body("branches[0].jobs[0].createdAt", `is`(project.branches[0].jobs[0].createdAt.toString()))
            .body("branches[0].jobs[1].id", `is`(project.branches[0].jobs[1].id))
            .body("branches[0].jobs[1].status", `is`(project.branches[0].jobs[1].status.toString()))
            .body("branches[0].jobs[1].createdAt", `is`(project.branches[0].jobs[1].createdAt.toString()))
            .body("branches[1].id", `is`(updatedProject.branches[1].id))
            .body("branches[1].name", `is`(updatedProject.branches[1].name))
            .body("branches[1].subDomain", `is`(updatedProject.branches[1].subDomain))
            .body("branches[1].jobs[0].id", `is`(project.branches[1].jobs[0].id))
            .body("branches[1].jobs[0].status", `is`(project.branches[1].jobs[0].status.toString()))
            .body("branches[1].jobs[0].createdAt", `is`(project.branches[1].jobs[0].createdAt.toString()))
            .body("branches[1].jobs[1].id", `is`(project.branches[1].jobs[1].id))
            .body("branches[1].jobs[1].status", `is`(project.branches[1].jobs[1].status.toString()))
            .body("branches[1].jobs[1].createdAt", `is`(project.branches[1].jobs[1].createdAt.toString()))

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