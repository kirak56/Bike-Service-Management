package com.tesla.bikeservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.service.CustomerService;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest  {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCustomerById() throws Exception {

    	Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");
        customer.setBikeModel("Tesla Bike X");
        customer.setCreatedAt(LocalDateTime.of(2025, 7, 16, 15, 0));

        when(customerService.getCustomer(1L)).thenReturn(customer);
    
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
    
    @Test
    void testCreateCustomer() throws Exception {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhone("+1987654321");
        newCustomer.setBikeModel("Tesla Bike Y");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(2L);
        savedCustomer.setName("Jane Smith");
        savedCustomer.setEmail("jane.smith@example.com");
        savedCustomer.setPhone("+1987654321");
        savedCustomer.setBikeModel("Tesla Bike Y");
        savedCustomer.setCreatedAt(LocalDateTime.of(2025, 7, 16, 15, 30));
        

        when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);
        
        String customerJson = "{\"name\":\"Jane Smith\",\"email\":\"jane.smith@example.com\",\"phone\":\"+1987654321\",\"bikeModel\":\"Tesla Bike Y\"}";
 
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.phone").value("+1987654321"))
                .andExpect(jsonPath("$.bikeModel").value("Tesla Bike Y"))
                .andExpect(jsonPath("$.createdAt").value("2025-07-16T15:30:00"));
                /*.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.phone").value("+1987654321"))
                .andExpect(jsonPath("$.bikeModel").value("Tesla Bike Y"))
                .andExpect(jsonPath("$.createdAt").value("2025-07-16T15:30:00"));
*/     }
    }
