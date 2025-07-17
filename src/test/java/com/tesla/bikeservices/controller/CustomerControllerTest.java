package com.tesla.bikeservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.service.CustomerService;

@WebMvcTest( CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;


    private Customer savedCustomer;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("John Doe");
        savedCustomer.setEmail("john.doe@example.com");
        savedCustomer.setPhone("+1234567890");
        savedCustomer.setBikeModel("Tesla Bike X");
        savedCustomer.setCreatedAt(LocalDateTime.of(2025, 7, 16, 15, 0));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhone("+1987654321");
        newCustomer.setBikeModel("Tesla Bike Y");

        Customer createdCustomer = new Customer();
        createdCustomer.setId(2L);
        createdCustomer.setName("Jane Smith");
        createdCustomer.setEmail("jane.smith@example.com");
        createdCustomer.setPhone("+1987654321");
        createdCustomer.setBikeModel("Tesla Bike Y");
        createdCustomer.setCreatedAt(LocalDateTime.of(2025, 7, 16, 15, 30));

        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

        String customerJson = "{\"name\":\"Jane Smith\",\"email\":\"jane.smith@example.com\",\"phone\":\"+1987654321\",\"bikeModel\":\"Tesla Bike Y\"}";

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.message").value("Customer created successfully"))
        .andExpect(jsonPath("$.data.id").value(2))
        .andExpect(jsonPath("$.data.name").value("Jane Smith"))
        .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"))
        .andExpect(jsonPath("$.data.phone").value("+1987654321"))
        .andExpect(jsonPath("$.data.bikeModel").value("Tesla Bike Y"))
        .andExpect(jsonPath("$.data.createdAt").exists());
    }

	/*
	 * @Test void testCreateCustomer_InvalidInput() throws Exception { String
	 * invalidCustomerJson =
	 * "{\"name\":\"\",\"email\":\"invalid\",\"phone\":\"123\",\"bikeModel\":\"\"}";
	 * 
	 * mockMvc.perform(post("/api/customers")
	 * .contentType(MediaType.APPLICATION_JSON) .content(invalidCustomerJson))
	 * .andExpect(status().isBadRequest()); }
	 */

    @Test
    void testGetCustomerById_Success() throws Exception {
        when(customerService.getCustomer(eq(1L))).thenReturn(savedCustomer);

        mockMvc.perform(get("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"))
                .andExpect(jsonPath("$.bikeModel").value("Tesla Bike X"))
                .andExpect(jsonPath("$.createdAt").value("2025-07-16T15:00:00"));
    }

	/*
	 * @Test void testGetCustomerById_NotFound() throws Exception {
	 * when(customerService.getCustomer(eq(999L))) .thenThrow(new
	 * EntityNotFoundException("Customer with ID 999 not found"));
	 * 
	 * mockMvc.perform(get("/api/customers/999")
	 * .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isNotFound())
	 * .andExpect(content().string("Customer with ID 999 not found")); }
	 * 
	 * @Test void testGetAllCustomers_Success() throws Exception { Page<Customer>
	 * page = new PageImpl<>(Arrays.asList(savedCustomer), pageable, 1);
	 * when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);
	 * 
	 * mockMvc.perform(get("/api/customers") .param("page", "0") .param("size",
	 * "10") .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isOk())
	 * .andExpect(jsonPath("$.content[0].id").value(1L))
	 * .andExpect(jsonPath("$.content[0].name").value("John Doe"))
	 * .andExpect(jsonPath("$.totalElements").value(1))
	 * .andExpect(jsonPath("$.totalPages").value(1)); }
	 * 
	 * @Test void testGetAllCustomers_EmptyPage() throws Exception { Page<Customer>
	 * emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
	 * when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(
	 * emptyPage);
	 * 
	 * mockMvc.perform(get("/api/customers") .param("page", "0") .param("size",
	 * "10") .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isOk())
	 * .andExpect(jsonPath("$.content").isEmpty())
	 * .andExpect(jsonPath("$.totalElements").value(0)); }
	 * 
	 * @Test void testUpdateCustomer_Success() throws Exception { Customer
	 * updatedCustomer = new Customer(); updatedCustomer.setId(1L);
	 * updatedCustomer.setName("John Doe Updated");
	 * updatedCustomer.setEmail("john.updated@example.com");
	 * updatedCustomer.setPhone("+1234567890");
	 * updatedCustomer.setBikeModel("Tesla Bike X1");
	 * updatedCustomer.setCreatedAt(LocalDateTime.of(2025, 7, 16, 15, 0));
	 * 
	 * when(customerService.updateCustomer(eq(1L),
	 * any(Customer.class))).thenReturn(updatedCustomer);
	 * 
	 * String updatedCustomerJson =
	 * "{\"name\":\"John Doe Updated\",\"email\":\"john.updated@example.com\",\"phone\":\"+1234567890\",\"bikeModel\":\"Tesla Bike X1\"}"
	 * ;
	 * 
	 * mockMvc.perform(put("/api/customers/1")
	 * .contentType(MediaType.APPLICATION_JSON) .content(updatedCustomerJson))
	 * .andExpect(status().isOk()) .andExpect(jsonPath("$.id").value(1L))
	 * .andExpect(jsonPath("$.name").value("John Doe Updated"))
	 * .andExpect(jsonPath("$.email").value("john.updated@example.com"))
	 * .andExpect(jsonPath("$.phone").value("+1234567890"))
	 * .andExpect(jsonPath("$.bikeModel").value("Tesla Bike X1"))
	 * .andExpect(jsonPath("$.createdAt").value("2025-07-16T15:00:00")); }
	 */
	/*
	 * @Test void testUpdateCustomer_NotFound() throws Exception {
	 * when(customerService.updateCustomer(eq(999L), any(Customer.class)))
	 * .thenThrow(new EntityNotFoundException("Customer with ID 999 not found"));
	 * 
	 * String customerJson =
	 * "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"phone\":\"+1234567890\",\"bikeModel\":\"Tesla Bike X\"}"
	 * ;
	 * 
	 * mockMvc.perform(put("/api/customers/999")
	 * .contentType(MediaType.APPLICATION_JSON) .content(customerJson))
	 * .andExpect(status().isNotFound())
	 * .andExpect(content().string("Customer with ID 999 not found")); }
	 * 
	 * @Test void testUpdateCustomer_InvalidInput() throws Exception { String
	 * invalidCustomerJson =
	 * "{\"name\":\"\",\"email\":\"invalid\",\"phone\":\"123\",\"bikeModel\":\"\"}";
	 * 
	 * mockMvc.perform(put("/api/customers/1")
	 * .contentType(MediaType.APPLICATION_JSON) .content(invalidCustomerJson))
	 * .andExpect(status().isBadRequest()); }
	 * 
	 * @Test void testDeleteCustomer_Success() throws Exception {
	 * doNothing().when(customerService).deleteCustomer(eq(1L));
	 * 
	 * mockMvc.perform(delete("/api/customers/1")
	 * .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isNoContent());
	 * }
	 * 
	 * @Test void testDeleteCustomer_NotFound() throws Exception { doThrow(new
	 * EntityNotFoundException("Customer with ID 999 not found"))
	 * .when(customerService).deleteCustomer(eq(999L));
	 * 
	 * mockMvc.perform(delete("/api/customers/999")
	 * .contentType(MediaType.APPLICATION_JSON)) .andExpect(status().isNotFound())
	 * .andExpect(content().string("Customer with ID 999 not found")); }
	 */
}