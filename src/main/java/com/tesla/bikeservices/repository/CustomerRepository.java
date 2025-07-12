package com.tesla.bikeservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tesla.bikeservices.entity.Customer;

@Repository 
public interface CustomerRepository extends  JpaRepository <Customer, Long> {

}