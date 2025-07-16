package com.tesla.bikeservices.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tesla.bikeservices.dto.ServiceBookingDTO;
import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.entity.ServiceBooking;
import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.repository.AppointmentSlotRepository;
import com.tesla.bikeservices.repository.CustomerRepository;
import com.tesla.bikeservices.repository.ServiceBookingRepository;
import com.tesla.bikeservices.repository.ServiceTypeRepository;
import com.tesla.bikeservices.repository.SparePartRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServiceBookingService {

	@Autowired
	public ServiceBookingRepository serviceBookingRepository;
	@Autowired
	public CustomerRepository customerRepository;
	@Autowired
	public ServiceTypeRepository serviceTypeRepository;
	@Autowired
	public SparePartRepository sparePartRepository;
	@Autowired
	public AppointmentSlotRepository appointmentSlotRepository;

	public ServiceBooking createBooking(ServiceBookingDTO bookingDTO) {
		Customer customer = customerRepository.findById(bookingDTO.getCustomerId()).
				 orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		ServiceType serviceType =serviceTypeRepository.findById(bookingDTO.getServiceTypeId()).orElseThrow(()
				 -> new EntityNotFoundException("Service type not found"));
		AppointmentSlot slot = appointmentSlotRepository.findById(bookingDTO.getAppointmentSlotId()).orElseThrow(()
				 -> new EntityNotFoundException("Appointment slot not found"));
		
		long slotDuration = java.time.Duration.between(slot.getStartTime(),
				  slot.getEndTime()).toMinutes();
		if (slotDuration <
				  serviceType.getEstimatedDurationMinutes()) { throw new
				  IllegalStateException("Appointment slot duration is insufficient for the service"
				 ); 
		}
		
		ServiceBooking booking = new ServiceBooking();
		booking.setCustomer(customer);
		booking.setServiceType(serviceType); booking.setAppointmentSlot(slot);
		  booking.setStatus(bookingDTO.getStatus());
		  booking.setPriority(bookingDTO.getPriority());
		  booking.setTechnician(bookingDTO.getTechnician());
		  booking.setNotes(bookingDTO.getNotes());
		  booking.getStatusHistory().add(bookingDTO.getStatus() + " at " +
		  LocalDateTime.now());
		  if (bookingDTO.getSparePartIds() != null)
		  {
			  List<SparePart> spareParts =  bookingDTO.getSparePartIds().stream() .map(id ->
				 sparePartRepository.findById(id) .orElseThrow(() -> new
				  EntityNotFoundException("Spare part not found: " + id)))
				  .collect(Collectors.toList());   
			  booking.setSpareParts(spareParts);
			  spareParts.forEach(part -> { part.setQuantity(part.getQuantity() - 1);
				 sparePartRepository.save(part); });
			  }
		  slot.setAvailable(false);
		  appointmentSlotRepository.save(slot);
		  
		  return serviceBookingRepository.save(booking);
	
	}
	
	/*
	 * if (bookingDTO.getSparePartIds() != null) { List<SparePart> spareParts =
	 * bookingDTO.getSparePartIds().stream() .map(id ->
	 * sparePartRepository.findById(id) .orElseThrow(() -> new
	 * EntityNotFoundException("Spare part not found: " + id)))
	 * .collect(Collectors.toList()); booking.setSpareParts(spareParts);
	 * spareParts.forEach(part -> { part.setQuantity(part.getQuantity() - 1);
	 * sparePartRepository.save(part); }); } slot.setAvailable(false);
	 * appointmentSlotRepository.save(slot);
	 * 
	 * return serviceBookingRepository.save(booking); }
	 * 
	 */	

	public ServiceBooking getBooking(Long id) {
		return serviceBookingRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Booking not found"));
	}
	// Search bookings by status, technician, or customer with pagination
    public Page<ServiceBooking> searchBookingsByCriteria(String status, String technicianPrefix, String customerPrefix, Pageable pageable) {
        return serviceBookingRepository.findByStatusOrTechnicianOrCustomerNameOrEmail(status, technicianPrefix, customerPrefix, pageable);
    }

	public List<ServiceBooking> getAllBookings() {
		return serviceBookingRepository.findAll();
	}

	public List<ServiceBooking> getBookingsByCustomer(Long customerId) {
		return serviceBookingRepository.findByCustomerId(customerId);
	}

	public ServiceBooking updateBooking(Long id, ServiceBookingDTO bookingDTO) {
		ServiceBooking booking = getBooking(id);
		Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		ServiceType serviceType = serviceTypeRepository.findById(bookingDTO.getServiceTypeId())
				.orElseThrow(() -> new EntityNotFoundException("Service type not found"));
		AppointmentSlot newSlot = appointmentSlotRepository.findById(bookingDTO.getAppointmentSlotId())
				.orElseThrow(() -> new EntityNotFoundException("Appointment slot not found"));

		// Validate new slot availability
		if (!newSlot.isAvailable()) {
			throw new IllegalStateException("Selected appointment slot is not available");
		}

		// Validate slot duration
		long slotDuration = java.time.Duration.between(newSlot.getStartTime(), newSlot.getEndTime()).toMinutes();
		if (slotDuration < serviceType.getEstimatedDurationMinutes()) {
			throw new IllegalStateException("Appointment slot duration is insufficient for the service");
		}

		// Free up the old slot
		if (booking.getAppointmentSlot() != null) {
			AppointmentSlot oldSlot = booking.getAppointmentSlot();
			oldSlot.setAvailable(true);
			appointmentSlotRepository.save(oldSlot);
		}

		// Update booking details
		if (!booking.getStatus().equals(bookingDTO.getStatus())) {
			booking.getStatusHistory().add(bookingDTO.getStatus() + " at " + LocalDateTime.now());
		}

		booking.setCustomer(customer);
		booking.setServiceType(serviceType);
		booking.setAppointmentSlot(newSlot);
		booking.setStatus(bookingDTO.getStatus());
		booking.setPriority(bookingDTO.getPriority());
		booking.setActualStartTime(bookingDTO.getActualStartTime());
		booking.setActualEndTime(bookingDTO.getActualEndTime());
		booking.setTechnician(bookingDTO.getTechnician());
		booking.setNotes(bookingDTO.getNotes());

		if (bookingDTO.getSparePartIds() != null) {
			List<SparePart> spareParts = bookingDTO.getSparePartIds().stream()
					.map(spId -> sparePartRepository.findById(spId)
							.orElseThrow(() -> new EntityNotFoundException("Spare part not found: " + spId)))
					.collect(Collectors.toList());
			booking.setSpareParts(spareParts);
		}

		// Mark new slot as booked
		newSlot.setAvailable(false);
		appointmentSlotRepository.save(newSlot);

		return serviceBookingRepository.save(booking);
	}

	public List<ServiceBooking> getBookingsByStatus(String status) {
		return serviceBookingRepository.findByStatus(status);
	}

	public void deleteBooking(Long id) {
		ServiceBooking booking = getBooking(id);
		if (booking.getAppointmentSlot() != null) {
			AppointmentSlot slot = booking.getAppointmentSlot();
			slot.setAvailable(true);
			appointmentSlotRepository.save(slot);
		}
		serviceBookingRepository.deleteById(id);
	}

}
