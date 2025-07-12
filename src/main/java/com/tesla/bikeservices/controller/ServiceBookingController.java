package com.tesla.bikeservices.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tesla.bikeservices.dto.ServiceBookingDTO;
import com.tesla.bikeservices.entity.ServiceBooking;
import com.tesla.bikeservices.service.ServiceBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class ServiceBookingController {

	@Autowired
	private ServiceBookingService serviceBookingService;

	@PostMapping
	public ResponseEntity<ServiceBooking> createBooking(@Valid @RequestBody ServiceBookingDTO bookingDTO) {
		return ResponseEntity.ok(serviceBookingService.createBooking(bookingDTO));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ServiceBooking> getBooking(@PathVariable Long id) {
		return ResponseEntity.ok(serviceBookingService.getBooking(id));
	}

	@GetMapping
	public ResponseEntity<List<ServiceBooking>> getAllBookings() {
		return ResponseEntity.ok(serviceBookingService.getAllBookings());
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<ServiceBooking>> getBookingsByCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(serviceBookingService.getBookingsByCustomer(customerId));
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<ServiceBooking>> getBookingsByStatus(@PathVariable String status) {
		return ResponseEntity.ok(serviceBookingService.getBookingsByStatus(status));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ServiceBooking> updateBooking(@PathVariable Long id,
			@Valid @RequestBody ServiceBookingDTO bookingDTO) {
		return ResponseEntity.ok(serviceBookingService.updateBooking(id, bookingDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
		serviceBookingService.deleteBooking(id);
		return ResponseEntity.noContent().build();
	}
}
