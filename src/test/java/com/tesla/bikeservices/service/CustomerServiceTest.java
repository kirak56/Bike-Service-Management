package com.tesla.bikeservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

	@BeforeEach
	void setUp() throws Exception {
		customerService = new CustomerService();
		customerService.customerRepository = customerRepository;

	}

	@Test
	void testCreateCustomer() {
		// arrange
		Customer customer = new Customer();
		customer.setName("John Doe");
		customer.setEmail("john@example.com");
		customer.setPhone("1234567890");
		customer.setBikeModel("Tesla Bike X");
		// act
		Customer savedCustomer = customerService.createCustomer(customer);
        //assertions
		assertNotNull(savedCustomer.getId());
		assertEquals("John Doe", savedCustomer.getName());
		assertEquals("john@example.com", savedCustomer.getEmail());
		assertEquals("1234567890", savedCustomer.getPhone());
		assertEquals("Tesla Bike X", savedCustomer.getBikeModel());
	}

	@Test
	void testGetCustomer() {
		//arrange
		Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane@example.com");
        customer.setPhone("0987654321");
        customer.setBikeModel("Tesla Bike Y");
        //act
        Customer savedCustomer = customerRepository.save(customer);

        Customer foundCustomer = customerService.getCustomer(savedCustomer.getId());
         //assert
        assertEquals(savedCustomer.getId(), foundCustomer.getId());
        assertEquals("Jane Doe", foundCustomer.getName());
	}
	
	@Test
    void testGetCustomerNotFound() {
        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomer(999L));
    }

	@Test
	void testGetAllCustomers() {
    //arrange
		Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhone("1234567890");
        customer1.setBikeModel("Tesla Bike X");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Jane Doe");
        customer2.setEmail("jane@example.com");
        customer2.setPhone("9876543218");
        customer2.setBikeModel("Tesla Bike Y");
        customerRepository.save(customer2);
      //act
        List<Customer> customers = customerService.getAllCustomers();
      //assertions
        assertEquals(2, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("John Doe")));
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Jane Doe")));
	}

	@Test
	void testUpdateCustomer() {
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

        Customer updatedCustomer = customerService.updateCustomer(savedCustomer.getId(), updatedDetails);

        assertEquals(savedCustomer.getId(), updatedCustomer.getId());
        assertEquals("John Smith", updatedCustomer.getName());
        assertEquals("john.smith@example.com", updatedCustomer.getEmail());
        assertEquals("9876543210", updatedCustomer.getPhone());
        assertEquals("Tesla Bike Z", updatedCustomer.getBikeModel());
		
	}

	@Test
	void testDeleteCustomer() {
		//arrange
		Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setBikeModel("Tesla Bike X");
        Customer savedCustomer = customerRepository.save(customer);
//act
        customerService.deleteCustomer(savedCustomer.getId());
//asserts
        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomer(savedCustomer.getId()));
	}

	@Test
	void testFindAllCustomersByNameOrEmailOrPhone() {
		Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhone("1234567890");
        customer1.setBikeModel("Tesla Bike X");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane@example.com");
        customer2.setPhone("9876543218");
        customer2.setBikeModel("Tesla Bike Y");
        customerRepository.save(customer2);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customers = customerService.findAllCustomersByNameOrEmailOrPhone("John", null, null, pageable);

        assertEquals(1, customers.getTotalElements());
        assertEquals("John Doe", customers.getContent().get(0).getName());
	}

}
