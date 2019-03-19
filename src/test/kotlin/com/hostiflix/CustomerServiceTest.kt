package com.hostiflix

import com.hostiflix.entity.Customer
import com.hostiflix.repository.CustomerRepository
import com.hostiflix.services.CustomerService
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
class CustomerServiceTest {

    @Mock
    private lateinit var customerRepository: CustomerRepository

    @InjectMocks
    private lateinit var customerService: CustomerService

    @Test
    fun `should return customer by id`() {

        /* Given */
        val id = "id"
        val customer = Customer("name1", "email1", "githubUsername1", "githubId1")

        given(customerRepository.findById(id)).willReturn(Optional.of(customer))

        /* When */
        val returnedCustomer = customerService.findCustomerById(id)

        /* Then */
        assertThat(returnedCustomer).isEqualTo(customer)

    }

    @Test
    fun `should return null when customer id is not found`() {

        /* Given */
        val id = "id"

        given(customerRepository.findById(id)).willReturn(Optional.empty())

        /* When */
        val returnedCustomer = customerService.findCustomerById(id)

        /* Then */
        assertThat(returnedCustomer).isNull()
    }
}