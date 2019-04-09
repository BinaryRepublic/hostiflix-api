package com.hostiflix

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.dto.GithubCustomerDto
import com.hostiflix.support.MockData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@JsonTest
class JsonObjectMapperTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should parse customer as json string`() {
        /* Given */
        val customer = MockData.customer("1")

        /* When */
        val jsonString = objectMapper.writeValueAsString(customer)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"id\":\"1\",\"name\":\"name_1\",\"email\":\"email_1\",\"githubUsername\":\"githubUsername_1\",\"githubId\":\"githubId_1\"}")
    }

    @Test
    fun `should parse project as json string`() {
        /* Given */
        val project =  MockData.project("1")
        project.branches.forEach { it.jobs.forEach { job -> job.id = "1" } }

        /* When */
        val jsonString = objectMapper.writeValueAsString(project)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"id\":\"1\",\"name\":\"name_1\",\"repositoryOwner\":\"repositoryOwner_1\",\"repositoryName\":\"repositoryName_1\",\"type\":\"NODEJS\",\"startCode\":\"startCode_1\",\"buildCode\":\"buildCode_1\",\"createdAt\":\"2019-04-01T00:00:00Z\",\"branches\":[{\"id\":\"1\",\"name\":\"name_1\",\"subDomain\":\"subDomain_1\",\"jobs\":[{\"id\":\"1\",\"status\":\"BUILD_SCHEDULED\",\"createdAt\":\"2019-04-01T00:00:00Z\"},{\"id\":\"1\",\"status\":\"BUILD_SCHEDULED\",\"createdAt\":\"2019-04-01T00:00:00Z\"}]},{\"id\":\"2\",\"name\":\"name_2\",\"subDomain\":\"subDomain_2\",\"jobs\":[{\"id\":\"1\",\"status\":\"BUILD_SCHEDULED\",\"createdAt\":\"2019-04-01T00:00:00Z\"},{\"id\":\"1\",\"status\":\"BUILD_SCHEDULED\",\"createdAt\":\"2019-04-01T00:00:00Z\"}]}],\"hash\":\"1\"}")
    }

    @Test
    fun `should serialize and deserialize Kotlin data class`() {
        /* Given */
        val githubCustomer = MockData.githubCustomerDto("g1")

        /* When */
        val jsonString = objectMapper.writeValueAsString(githubCustomer)
        val deserialized = objectMapper.readValue(jsonString, GithubCustomerDto::class.java)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"id\":\"testId\",\"name\":\"name_g1\",\"login\":\"login_g1\"}")
        assertThat(deserialized).isEqualToComparingFieldByFieldRecursively(githubCustomer)
    }
}