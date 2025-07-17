package com.tesla.bikeservices.controller;

import java.util.List;

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

import com.tesla.bikeservices.dto.ServiceBookingDTO;
import com.tesla.bikeservices.entity.ServiceBooking;
import com.tesla.bikeservices.response.ApiResponse;
import com.tesla.bikeservices.service.ServiceBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class ServiceBookingController {

    private final ServiceBookingService serviceBookingService;


    public ServiceBookingController(ServiceBookingService serviceBookingService ) {
        this.serviceBookingService = serviceBookingService;
        
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceBooking>> createBooking(@Valid @RequestBody ServiceBookingDTO bookingDTO) {
        ServiceBooking booking = serviceBookingService.createBooking(bookingDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceBooking>> getBooking(@PathVariable Long id) {
        ServiceBooking booking = serviceBookingService.getBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceBooking>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceBooking> bookings = serviceBookingService.getAllBookings(pageable);
        
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, bookings.getTotalElements(), bookings.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Bookings retrieved successfully", bookings , pagination));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<ServiceBooking>>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<ServiceBooking> bookings = serviceBookingService.getBookingsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ServiceBooking>>> getBookingsByStatus(@PathVariable String status) {
        List<ServiceBooking> bookings = serviceBookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceBooking>> updateBooking(@PathVariable Long id, @Valid @RequestBody ServiceBookingDTO bookingDTO) {
        ServiceBooking updatedBooking = serviceBookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", updatedBooking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        serviceBookingService.deleteBooking(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Booking deleted successfully", null));
    }

    @GetMapping("/search-by-criteria")
    public ResponseEntity<ApiResponse<Page<ServiceBooking>>> searchBookingsByCriteria(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String technicianPrefix,
            @RequestParam(required = false) String customerPrefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String sanitizedStatus = status != null ? status.trim() : null;
        String sanitizedTechnician = technicianPrefix != null ? technicianPrefix.trim() : null;
        String sanitizedCustomer = customerPrefix != null ? customerPrefix.trim() : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceBooking> bookings = serviceBookingService.searchBookingsByCriteria(
                sanitizedStatus, sanitizedTechnician, sanitizedCustomer, pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, bookings.getTotalElements(), bookings.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Bookings retrieved successfully", bookings, pagination));
    }
}