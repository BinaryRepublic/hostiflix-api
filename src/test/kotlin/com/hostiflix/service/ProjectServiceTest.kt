package com.hostiflix.service

import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
class ProjectServiceTest {

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var projectRepository: ProjectRepository

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
}