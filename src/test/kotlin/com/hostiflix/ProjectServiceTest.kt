package com.hostiflix

import com.hostiflix.entity.Branch
import com.hostiflix.entity.Project
import com.hostiflix.repository.ProjectRepository
import com.hostiflix.services.ProjectService
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
        val projectId = "projectId"
        val project = Project(
            "customerId1",
            "name1",
            "repository1",
            "projectType1",
            emptyList()
        )
        val branch1 = Branch(project, "name1").apply { id ="randomString1" }
        val branch2 = Branch(project, "name2").apply { id ="randomString2" }

        val listOfBranches = listOf(branch1, branch2)
        project.apply { branches = listOfBranches }
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project))

        /* When */
        val returnedProject = projectService.findProjectById(projectId)

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
