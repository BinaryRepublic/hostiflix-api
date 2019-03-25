package com.hostiflix

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.Branch
import com.hostiflix.entity.Customer
import com.hostiflix.entity.Project
import io.restassured.config.JsonConfig
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JsonConfig::class, JacksonAutoConfiguration::class])
class JsonObjectMapperTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should parse customer as json string`() {
        /* Given */
        val customer = Customer(
            "name5",
            "email5",
            "githubUsername5",
            "githubId5"
        ).apply { id = "randomString" }

        /* When */
        val jsonString = objectMapper.writeValueAsString(customer)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"name\":\"name5\",\"email\":\"email5\",\"githubUsername\":\"githubUsername5\",\"githubId\":\"githubId5\",\"id\":\"randomString\"}")
    }

    @Test
    fun `should parse project as json string`() {
        /* Given */
        val project =  Project(
            "customerId5",
            "name5",
            "repository5",
            "projectType5",
            emptyList()
        ).apply { id = "randomString5" }
        val branch5 = Branch(project, "name5").apply { id = "randomString5" }
        val branch6 = Branch(project, "name6").apply { id = "randomString6" }
        val listOfBranches4 = listOf(branch5, branch6)
        project.apply { branches = listOfBranches4 }

        /* When */
        val jsonString = objectMapper.writeValueAsString(project)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"customerId\":\"customerId5\",\"name\":\"name5\",\"repository\":\"repository5\",\"projectType\":\"projectType5\",\"branches\":[{\"name\":\"name5\",\"id\":\"randomString5\"},{\"name\":\"name6\",\"id\":\"randomString6\"}],\"id\":\"randomString5\"}")
    }
}