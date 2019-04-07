package com.hostiflix.service

import com.hostiflix.repository.ProjectRepository
import com.hostiflix.support.MockData
import com.hostiflix.webservice.deploymentWs.DeploymentWsImpl
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
class GithubServiceTest {

    @Mock
    private lateinit var projectRepository: ProjectRepository

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var deploymentWsImpl: DeploymentWsImpl

    @InjectMocks
    private lateinit var githubService: GithubService

    @Test
    fun `should filter webhooks and trigger deployment`() {
        /* Given */
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto().apply { ref = "refs/heads/name_1" }
        val project = MockData.project("1")
        val authCredentials = MockData.authCredentials("1")
        val deploymentServiceRequestDto = MockData.deploymentServiceRequestDto("1")
        val job = MockData.job("1", project.branches[0])
        given(projectRepository.findByRepositoryOwnerAndRepositoryName(githubWebhookResponseDto.repository.owner.name, githubWebhookResponseDto.repository.name)).willReturn(project)
        given(authenticationService.findAuthCredentialsByCustomerId(project.customerId!!)).willReturn(authCredentials)
        given(deploymentWsImpl.postWebhook(com.nhaarman.mockito_kotlin.check {
            assertThat(it).isEqualToComparingFieldByFieldRecursively(deploymentServiceRequestDto)
        })).willReturn(job)

        /* When */
        val returnedHttpStatus = githubService.filterWebHooksAndTriggerDeployment(githubWebhookResponseDto)

        /* Then */
        assertThat(returnedHttpStatus).isEqualTo(HttpStatus.ACCEPTED)
        verify(projectRepository).save(project)
    }

    @Test
    fun `should return bad request because no deployment should be triggered`() {
        /* Given */
        val githubWebhookResponseDto = MockData.githubWebhookResponseDto()
        val project = MockData.project("1")
        given(projectRepository.findByRepositoryOwnerAndRepositoryName(githubWebhookResponseDto.repository.owner.name, githubWebhookResponseDto.repository.name)).willReturn(project)

        /* When */
        val returnedHttpStatus = githubService.filterWebHooksAndTriggerDeployment(githubWebhookResponseDto)

        /* Then */
        assertThat(returnedHttpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}