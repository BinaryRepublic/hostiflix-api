package com.hostiflix.controller

import com.hostiflix.entity.JobStatus
import com.hostiflix.service.JobService
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.given
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JacksonAutoConfiguration::class])
@WebMvcTest
class JobControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var jobService: JobService

    @InjectMocks
    private lateinit var jobController: JobController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(jobController)
            .build()
    }

    @Test
    fun `should return 404 when updating job status for invalid job id`() {
        // given
        val id = "invalid-id"
        val newJobStatus = JobStatus.DEPLOYMENT_SUCCESSFUL
        given(jobService.updateJobStatusById(id, newJobStatus)).willReturn(null)

        // when, then
        mockMvc
            .perform(MockMvcRequestBuilders.put("/jobs/$id/status/${newJobStatus.toString()}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should return no content when updating job status went successful`() {
        // given
        val id = "1234"
        val newJobStatus = JobStatus.DEPLOYMENT_SUCCESSFUL
        val job = MockData.job("j1", MockData.branch("b1", MockData.project("p1", "c1")))
        given(jobService.updateJobStatusById(id, newJobStatus)).willReturn(job)

        // when, then
        mockMvc
            .perform(MockMvcRequestBuilders.put("/jobs/$id/status/${newJobStatus.toString()}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}