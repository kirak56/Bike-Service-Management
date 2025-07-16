package com.tesla.bikeservices.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tesla.bikeservices.entity.ServiceBooking;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByCustomerId(Long customerId);
    List<ServiceBooking> findByStatus(String status);
    
 // Search bookings by status, technician, customer.name, or customer.email with pagination
    @Query("SELECT b FROM ServiceBooking b WHERE " +
    	       "(b.status = :status) OR " +
    	       "(LOWER(b.technician) LIKE LOWER(CONCAT(:technicianPrefix, '%'))) OR " +
    	       "(LOWER(b.customer.name) LIKE LOWER(CONCAT(:customerPrefix, '%')) OR " +
    	       "LOWER(b.customer.email) LIKE LOWER(CONCAT(:customerPrefix, '%')))")
    Page<ServiceBooking> findByStatusOrTechnicianOrCustomerNameOrEmail(
            @Param("status") String status,
            @Param("technicianPrefix") String technicianPrefix,
            @Param("customerPrefix") String customerPrefix,
            Pageable pageable);
}
