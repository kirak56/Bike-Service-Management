package com.tesla.bikeservices.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.response.ApiResponse;
import com.tesla.bikeservices.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@Valid @RequestBody Customer customer) {
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", savedCustomer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Customer>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerService.getAllCustomers(pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, customers.getTotalElements(), customers.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Customers retrieved successfully", customers, pagination));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updatedCustomer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Customer deleted successfully", null));
    }

    @GetMapping("/search-by-criteria")
    public ResponseEntity<ApiResponse<Page<Customer>>> fetchCustomersByCriteria(
            @RequestParam(required = false) String namePrefix,
            @RequestParam(required = false) String emailPrefix,
            @RequestParam(required = false) String phonePrefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String sanitizedName = namePrefix != null ? namePrefix.trim() : null;
        String sanitizedEmail = emailPrefix != null ? emailPrefix.trim() : null;
        String sanitizedPhone = phonePrefix != null ? phonePrefix.trim() : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerService.findAllCustomersByNameOrEmailOrPhone(
                sanitizedName, sanitizedEmail, sanitizedPhone, pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, customers.getTotalElements(), customers.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Customers retrieved successfully", customers, pagination));
    }
}