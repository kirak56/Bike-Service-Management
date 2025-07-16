package com.tesla.bikeservices.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {


	@Autowired
    public  CustomerRepository customerRepository;
	
	public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
	public Customer getCustomer(Long id) {
		return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }
	
	public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
	
	public Customer updateCustomer(Long id, Customer customerDetails) {
		Customer customer = getCustomer(id);
		customer.setName(customerDetails.getName());
		customer.setEmail(customerDetails.getEmail());
		customer.setPhone(customerDetails.getPhone());
		customer.setBikeModel(customerDetails.getBikeModel());
		return customerRepository.save(customer);
    }
	public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
	
	public Page<Customer> findAllCustomersByNameOrEmailOrPhone(String namePrefix, String emailPrefix, String phonePrefix, Pageable pageable) {
        return customerRepository.findAllCustomersByNameOrEmailOrPhone(namePrefix, emailPrefix, phonePrefix, pageable);
    }
	
	
	
}
