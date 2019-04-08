package com.hostiflix.service

import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import com.hostiflix.webservice.githubWs.GithubWs
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ProjectServiceTest {

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var projectRepository: ProjectRepository

    @Mock
    private lateinit var githubWs: GithubWs

    @InjectMocks
    private lateinit var projectService: ProjectService

    @Test
    fun `should return project by id`() {
        /* Given */
        val project = MockData.project("1")
        val accessToken = "accessToken"
        val customerId = "c1"
        given(authenticationService.getCustomerIdByAccessToken(accessToken)).willReturn(customerId)
        given(projectRepository.findByIdAndCustomerId(project.id!!, customerId)).willReturn(project)

        /* When */
        val returnedProject = projectService.findProjectByIdAndAccessToken(project.id!!, accessToken)

        /* Then */
        assertThat(returnedProject).isEqualTo(project)
    }

    @Test
    fun `should return null when project id is not found`() {
        /* Given */
        val id = "id"
        val accessToken = "accessToken"
        val customerId = "c1"
        given(authenticationService.getCustomerIdByAccessToken(accessToken)).willReturn(customerId)
        given(projectRepository.findByIdAndCustomerId(id, customerId)).willReturn(null)

        /* When */
        val returnedProject = projectService.findProjectByIdAndAccessToken(id, accessToken)

        /* Then */
        assertThat(returnedProject).isNull()
    }

    @Test
    fun `should set customer id by access token and save project`() {
        // given
        val project = MockData.project("p1")
        project.customerId = null
        val accessToken = "accessToken"
        val customerId = "c1"
        val projectWithCustomerId = project.copy(customerId = customerId)

        given(authenticationService.getCustomerIdByAccessToken(accessToken)).willReturn(customerId)
        given(projectRepository.save<Project>(check {
            assertThat(it.id).isEqualTo(project.id)
            assertThat(it.customerId).isEqualTo(customerId)
        })).willReturn(projectWithCustomerId)

        // when
        val savedProject = projectService.saveProject(project, accessToken)

        // then
        assertThat(savedProject).isEqualToComparingFieldByFieldRecursively(projectWithCustomerId)
    }

    @Test
    fun `should keep existing jobs when updating the project`() {
        // given
        val accessToken = "accessToken"
        val customerId = "c1"
        val newProject = MockData.project("p1", customerId).apply {
            branches.forEach {
                it.jobs = mutableListOf(MockData.job("j1", it))
            }
        }
        val currentProject = MockData.project("p1", customerId).apply {
            branches.forEach {
                it.jobs = mutableListOf(MockData.job("j1", it), MockData.job("j2", it))
            }
        }
        given(projectRepository.findById(newProject.id!!)).willReturn(Optional.of(currentProject))
        given(authenticationService.getCustomerIdByAccessToken(accessToken)).willReturn(customerId)
        given(projectRepository.save<Project>(check {
            assertThat(it.branches[0].jobs.size).isEqualTo(2)
            assertThat(it.branches[1].jobs.size).isEqualTo(2)
        })).willReturn(newProject)

        // when
        val resultingProject = projectService.updateProject(newProject, accessToken)

        // then
        assertThat(resultingProject.branches[0].jobs.size).isEqualTo(2)
        assertThat(resultingProject.branches[1].jobs.size).isEqualTo(2)
    }
}