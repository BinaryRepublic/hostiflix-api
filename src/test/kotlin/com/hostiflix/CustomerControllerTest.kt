package com.hostiflix

import com.hostiflix.controller.CustomerController
import com.hostiflix.entity.Customer
import com.hostiflix.services.CustomerService
import com.nhaarman.mockito_kotlin.given
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(MockitoJUnitRunner::class)
@WebMvcTest
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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

        val customer1 = Customer("name1", "email1", "githubUsername1", "githubId1")
        customer1.id = "randomString1"
        val customer2 = Customer("name2", "email2", "githubUsername2", "githubId2")
        customer2.id = "randomString2"
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

        val customer3 =  Customer("name3", "email3", "githubUsername3", "githubId3")
        customer3.id = "randomString3"

        given(customerService.findCustomerById(customer3.id)).willReturn(customer3)

        /* When, Then */

        mockMvc
            .perform(get("/customers/${customer3.id}"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(customer3.id)))
            .andExpect(jsonPath("$.name", `is`(customer3.name)))
            .andExpect(jsonPath("$.email", `is`(customer3.email)))
            .andExpect(jsonPath("$.githubUsername", `is`(customer3.githubUsername)))
            .andExpect(jsonPath("$.githubId", `is`(customer3.githubId)))
    }

    @Test
    fun `should return 400 when no customer with given Id is found (findById)`() {

        /* Given */

        val customer4 =  Customer("name4", "email4", "githubUsername4", "githubId4")
        customer4.id = "randomString4"

        given(customerService.findCustomerById(customer4.id)).willReturn(null)

        /* When, Then */

        mockMvc
            .perform(get("/customers/${customer4.id}"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Customer ID not found")))
    }

    @Test
    fun `should return updated customer`() {

        /* Given */

        val initialCustomer =  Customer("name5", "email5", "githubUsername5", "githubId5")
        initialCustomer.id = "randomString5"
        val newCustomer =  Customer("updated", "updated", "updated", "githubId5")
        newCustomer.apply { id = initialCustomer.id }

        given(customerService.existsById(newCustomer.id)).willReturn(true)

        /* When, Then */

        val newCustomerConvertedToString = newCustomer.toString()

        mockMvc
            .perform(
                put("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newCustomerConvertedToString)
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
    fun `should return 400 when no customer with given Id is found (update)`() {

        /* Given */

        val newCustomer =  Customer("name6", "email6", "githubUsername6", "githubId6")
        newCustomer.id = "randomString6"

        given(customerService.existsById(newCustomer.id)).willReturn(false)

        /* When, Then */

        val newCustomerConvertedToString = newCustomer.toString()

        mockMvc
            .perform(
                put("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newCustomerConvertedToString)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", `is`("Customer ID not found")))
    }

    @Test
    fun `should return 204 no content`() {

        /* When, Then */

        mockMvc
            .perform(
                delete("/customers/randomId"))
            .andDo(print())
            .andExpect(status().isNoContent)
    }
}