package com.hostiflix.integrationTests

import com.hostiflix.entity.JobStatus
import com.hostiflix.support.MockData
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus

class JobIntegrationTest: BaseIntegrationTest() {

    @Before
    fun setUp() {
        RestAssured.basePath = "/jobs"
    }

    @Test
    fun `should update job status`() {
        // given
        var project = MockData.project("p1", testCustomer!!.id!!)
        project = projectRepository.save(project)
        var job = project.branches.first().jobs.first()
        job.status = JobStatus.BUILD_SCHEDULED
        job = jobRepository.save(job)
        val newStatus = JobStatus.DEPLOYMENT_SUCCESSFUL

        // when, then
        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .put("/${job.id}/status/${newStatus}")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NO_CONTENT.value())

        val resultingJob = jobRepository.findById(job.id).get()
        assertThat(resultingJob.status).isEqualTo(JobStatus.DEPLOYMENT_SUCCESSFUL)
    }

    @Test
    fun `should return 404 when updating status of non existing job`() {
        // given, when, then
        RestAssured
            .given()
            .header("Access-Token", accessToken)
            .put("/invalid-id/status/DEPLOYMENT_SUCCESSFUL")
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }
}