package com.tesla.bikeservices.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Async 
    public CompletableFuture<Customer> createCustomer(Customer customer) {
    	Customer savedCustomer = customerRepository.save(customer);
        return CompletableFuture.completedFuture(savedCustomer);
    }

    public Customer getCustomer(Long id) {
        return findCustomerOrThrow(id);
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = findCustomerOrThrow(id);
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setBikeModel(customerDetails.getBikeModel());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        findCustomerOrThrow(id);
        customerRepository.deleteById(id);
    }
    @Async
    public CompletableFuture<Page<Customer>> findAllCustomersByNameOrEmailOrPhone(String namePrefix, String emailPrefix, String phonePrefix, Pageable pageable) {
    	Page<Customer> customers = customerRepository.findAllCustomersByNameOrEmailOrPhone(namePrefix, emailPrefix, phonePrefix, pageable);
    	return CompletableFuture.completedFuture(customers);
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found"));
    }
}