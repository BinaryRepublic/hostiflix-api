package com.hostiflix.service

import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import java.util.*

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
class ProjectServiceTest {

    @Mock
    private lateinit var projectRepository: ProjectRepository

    @InjectMocks
    private lateinit var projectService: ProjectService

    @Test
    fun `should return project by id`() {
        /* Given */
        val project = MockData.project("1")

        given(projectRepository.findById(project.id!!)).willReturn(Optional.of(project))

        /* When */
        val returnedProject = projectService.findProjectById(project.id!!)

        /* Then */
        assertThat(returnedProject).isEqualTo(project)
    }

    @Test
    fun `should return null when project id is not found`() {
        /* Given */
        val id = "id"
        given(projectRepository.findById(id)).willReturn(Optional.empty())

        /* When */
        val returnedProject = projectService.findProjectById(id)

        /* Then */
        assertThat(returnedProject).isNull()
    }
}