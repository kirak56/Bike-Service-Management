package com.tesla.bikeservices.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tesla.bikeservices.entity.Customer;

@Repository 
public interface CustomerRepository extends  JpaRepository <Customer, Long> {
	
	@Query("SELECT c FROM Customer c WHERE (:namePrefix IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT(:namePrefix, '%'))) " +
	           "OR (:emailPrefix IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT(:emailPrefix, '%'))) " +
	           "OR (:phonePrefix IS NULL OR c.phone LIKE CONCAT(:phonePrefix, '%'))")
	    Page<Customer> findAllCustomersByNameOrEmailOrPhone(@Param("namePrefix") String namePrefix,
	                                                       @Param("emailPrefix") String emailPrefix,
	                                                       @Param("phonePrefix") String phonePrefix,
	                                                       Pageable pageable);

}