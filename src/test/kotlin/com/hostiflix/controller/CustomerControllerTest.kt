package com.hostiflix.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hostiflix.entity.Customer
import com.hostiflix.service.CustomerService
import com.hostiflix.support.MockData
import com.nhaarman.mockito_kotlin.given
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [JacksonAutoConfiguration::class])
@WebMvcTest
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var customerService: CustomerService

    @InjectMocks
    lateinit var customerController: CustomerController

    @Before
    fun init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(customerController)
            .build()
    }

	@Test
	fun `should return a list of all customers`() {
        /* Given */
        val customer1 = MockData.customer("1")
        val customer2 = MockData.customer("2")
        val customerList = listOf(customer1, customer2)
        given(customerService.findAllCustomers()).willReturn(customerList)

        /* When, Then */
        mockMvc
            .perform(get("/customers"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.customers", hasSize<Customer>(2)))
            .andExpect(jsonPath("$.customers[0].id", `is`(customer1.id)))
            .andExpect(jsonPath("$.customers[0].name", `is`(customer1.name)))
            .andExpect(jsonPath("$.customers[0].email", `is`(customer1.email)))
            .andExpect(jsonPath("$.customers[0].githubUsername", `is`(customer1.githubUsername)))
            .andExpect(jsonPath("$.customers[0].githubId", `is`(customer1.githubId)))
            .andExpect(jsonPath("$.customers[1].id", `is`(customer2.id)))
            .andExpect(jsonPath("$.customers[1].name", `is`(customer2.name)))
            .andExpect(jsonPath("$.customers[1].email", `is`(customer2.email)))
            .andExpect(jsonPath("$.customers[1].githubUsername", `is`(customer2.githubUsername)))
            .andExpect(jsonPath("$.customers[1].githubId", `is`(customer2.githubId)))
    }


    @Test
    fun `should return a customer by Id`() {
        /* Given */
        val customer =  MockData.customer("3")
        given(customerService.findCustomerById(customer.id!!)).willReturn(customer)

        /* When, Then */
        mockMvc
            .perform(get("/customers/${customer.id}"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(customer.id)))
            .andExpect(jsonPath("$.name", `is`(customer.name)))
            .andExpect(jsonPath("$.email", `is`(customer.email)))
            .andExpect(jsonPath("$.githubUsername", `is`(customer.githubUsername)))
            .andExpect(jsonPath("$.githubId", `is`(customer.githubId)))
    }

    @Test
    fun `should return error 400 when no customer with given Id is found (findById)`() {
        /* Given */
        val customer =  MockData.customer("4")
        given(customerService.findCustomerById(customer.id!!)).willReturn(null)

        /* When, Then */
        mockMvc
            .perform(get("/customers/${customer.id}"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Customer ID not found")))
    }

    @Test
    fun `should return updated customer`() {
        /* Given */
        val initialCustomer =  MockData.customer("5")
        val newCustomer =  MockData.customer("5").apply {
            name = "updated"
            email = "updated"
        }
        given(customerService.existsById(newCustomer.id!!)).willReturn(true)
        val body = objectMapper.writeValueAsString(newCustomer)

        /* When, Then */
        mockMvc
            .perform(
                put("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(initialCustomer.id)))
            .andExpect(jsonPath("$.name", `is`(newCustomer.name)))
            .andExpect(jsonPath("$.email", `is`(newCustomer.email)))
            .andExpect(jsonPath("$.githubUsername", `is`(newCustomer.githubUsername)))
            .andExpect(jsonPath("$.githubId", `is`(newCustomer.githubId)))
    }

    @Test
    fun `should return error 400 when no customer with given Id is found (update)`() {
        /* Given */
        val newCustomer =  MockData.customer("6")
        given(customerService.existsById(newCustomer.id!!)).willReturn(false)
        val body = objectMapper.writeValueAsString(newCustomer)

        /* When, Then */
        mockMvc
            .perform(
                put("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Customer ID not found")))
    }

    @Test
    fun `should return error 204 no content`() {
        /* When, Then */
        mockMvc
            .perform(
                delete("/customers/randomId"))
            .andDo(print())
            .andExpect(status().isNoContent)
    }
}