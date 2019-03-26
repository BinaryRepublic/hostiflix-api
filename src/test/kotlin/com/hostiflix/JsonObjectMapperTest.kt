package com.hostiflix

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.support.MockData
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JacksonAutoConfiguration::class])
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

        /* When */
        val jsonString = objectMapper.writeValueAsString(project)

        /* Then */
        assertThat(jsonString).isEqualTo("{\"id\":\"1\",\"customerId\":\"customerId_1\",\"name\":\"name_1\",\"repository\":\"repository_1\",\"projectType\":\"projectType_1\",\"branches\":[{\"id\":\"1\",\"name\":\"name_1\"},{\"id\":\"2\",\"name\":\"name_2\"}]}")
    }
}