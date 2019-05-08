package com.hostiflix.service

import com.hostiflix.entity.JobStatus
import com.hostiflix.repository.JobRepository
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.*

@RunWith(SpringJUnit4ClassRunner::class)
class JobServiceTest {

    @Mock
    private lateinit var jobRepository: JobRepository

    @InjectMocks
    private lateinit var jobService: JobService

    @Test
    fun `should return null if job id is not existing`() {
        // given
        val id = "invalid-id"
        given(jobRepository.findById(id)).willReturn(Optional.empty())

        // when
        val result = jobService.updateJobStatusById(id, JobStatus.BUILD_FAILED)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `should return job after job was update successfully`() {
        // given
        val id = "1234"
        val newStatus = JobStatus.DEPLOYMENT_SUCCESSFUL
        val job = MockData.job("j1", MockData.branch("b1", MockData.project("p1", "c1")))

        given(jobRepository.findById(id)).willReturn(Optional.of(job))
        given(jobRepository.save(job)).willReturn(job)

        // when
        val result = jobService.updateJobStatusById(id, newStatus)

        // then
        assertThat(result).isNotNull
        assertThat(result!!.status).isEqualTo(newStatus)
    }
}