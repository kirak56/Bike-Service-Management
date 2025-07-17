package com.tesla.bikeservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;

@DataJpaTest
class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerService customerService;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Initialize CustomerService with autowired repository
        customerService = new CustomerService(customerRepository);
        // Initialize pageable for pagination tests
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testCreateCustomer_Success() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setBikeModel("Tesla Bike X");

        // Act
        Customer savedCustomer = customerService.createCustomer(customer);

        // Assert
        assertNotNull(savedCustomer.getId());
        assertEquals("John Doe", savedCustomer.getName());
        assertEquals("john@example.com", savedCustomer.getEmail());
        assertEquals("1234567890", savedCustomer.getPhone());
        assertEquals("Tesla Bike X", savedCustomer.getBikeModel());
        // Note: Logger.info("Creating customer with email: {}") is called here
    }

    @Test
    void testGetCustomer_Success() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane@example.com");
        customer.setPhone("0987654321");
        customer.setBikeModel("Tesla Bike Y");
        Customer savedCustomer = customerRepository.save(customer);

        // Act
        Customer foundCustomer = customerService.getCustomer(savedCustomer.getId());

        // Assert
        assertNotNull(foundCustomer);
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
        assertEquals("Jane Doe", foundCustomer.getName());
        assertEquals("jane@example.com", foundCustomer.getEmail());
        assertEquals("0987654321", foundCustomer.getPhone());
        assertEquals("Tesla Bike Y", foundCustomer.getBikeModel());
        // Note: Logger.debug("Fetching customer with ID: {}") is called here
    }

    @Test
    void testGetCustomer_NotFound() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.getCustomer(999L));
        assertEquals("Customer with ID 999 not found", exception.getMessage());
        // Note: Logger.debug("Fetching customer with ID: {}") is called here
    }

    @Test
    void testGetAllCustomers_Success() {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhone("1234567890");
        customer1.setBikeModel("Tesla Bike X");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Jane Doe");
        customer2.setEmail("jane@example.com");
        customer2.setPhone("9876543210");
        customer2.setBikeModel("Tesla Bike Y");
        customerRepository.save(customer2);

        // Act
        Page<Customer> result = customerService.getAllCustomers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().anyMatch(c -> c.getName().equals("John Doe")));
        assertTrue(result.getContent().stream().anyMatch(c -> c.getName().equals("Jane Doe")));
        // Note: Logger.debug("Fetching all customers with pagination: page={}, size={}") is called here
    }

    @Test
    void testGetAllCustomers_EmptyPage() {
        // Act
        Page<Customer> result = customerService.getAllCustomers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        // Note: Logger.debug("Fetching all customers with pagination: page={}, size={}") is called here
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setBikeModel("Tesla Bike X");
        Customer savedCustomer = customerRepository.save(customer);

        Customer updatedDetails = new Customer();
        updatedDetails.setName("John Smith");
        updatedDetails.setEmail("john.smith@example.com");
        updatedDetails.setPhone("9876543210");
        updatedDetails.setBikeModel("Tesla Bike Z");

        // Act
        Customer updatedCustomer = customerService.updateCustomer(savedCustomer.getId(), updatedDetails);

        // Assert
        assertNotNull(updatedCustomer);
        assertEquals(savedCustomer.getId(), updatedCustomer.getId());
        assertEquals("John Smith", updatedCustomer.getName());
        assertEquals("john.smith@example.com", updatedCustomer.getEmail());
        assertEquals("9876543210", updatedCustomer.getPhone());
        assertEquals("Tesla Bike Z", updatedCustomer.getBikeModel());
        // Note: Logger.info("Updating customer with ID: {}") is called here
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        Customer updatedDetails = new Customer();
        updatedDetails.setName("John Smith");
        updatedDetails.setEmail("john.smith@example.com");
        updatedDetails.setPhone("9876543210");
        updatedDetails.setBikeModel("Tesla Bike Z");

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.updateCustomer(999L, updatedDetails));
        assertEquals("Customer with ID 999 not found", exception.getMessage());
        // Note: Logger.info("Updating customer with ID: {}") is called here
    }

    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setBikeModel("Tesla Bike X");
        Customer savedCustomer = customerRepository.save(customer);

        // Act
        customerService.deleteCustomer(savedCustomer.getId());

        // Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.getCustomer(savedCustomer.getId()));
        assertEquals("Customer with ID " + savedCustomer.getId() + " not found", exception.getMessage());
        // Note: Logger.info("Deleting customer with ID: {}") is called here
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.deleteCustomer(999L));
        assertEquals("Customer with ID 999 not found", exception.getMessage());
        // Note: Logger.info("Deleting customer with ID: {}") is called here
    }

    @Test
    void testFindAllCustomersByNameOrEmailOrPhone_Success() {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhone("1234567890");
        customer1.setBikeModel("Tesla Bike X");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane@example.com");
    }
}