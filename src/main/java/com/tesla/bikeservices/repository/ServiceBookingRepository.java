package com.tesla.bikeservices.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tesla.bikeservices.entity.ServiceBooking;
@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByCustomerId(Long customerId);
    List<ServiceBooking> findByStatus(String status);
}
