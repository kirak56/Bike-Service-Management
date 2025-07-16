package com.tesla.bikeservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import jakarta.transaction.Transactional;
@Transactional
@SpringBootTest
class ServiceBookingServiceTest extends ServiceBookingService {
	@Autowired
    private ServiceBookingService serviceBookingService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private ServiceBookingRepository serviceBookingRepository;
	
    private Customer customer;
    private ServiceType serviceType;
    private AppointmentSlot appointmentSlot;
    private SparePart sparePart;
    private ServiceBookingDTO bookingDTO;
    
	@BeforeEach
	void setUp() throws Exception  {
		
		
		serviceBookingRepository.deleteAll();
        appointmentSlotRepository.deleteAll();
        sparePartRepository.deleteAll();
        serviceTypeRepository.deleteAll();
        customerRepository.deleteAll();
     
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("1234567890"); // Set mandatory phone field
        customer.setBikeModel("Tesla Bike X1"); // Set mandatory bikeModel field
        customer = customerRepository.save(customer);

        serviceType = new ServiceType();
        serviceType.setName("Oil Change");
        serviceType.setEstimatedDurationMinutes(60); // Satisfies @Positive
        serviceType.setCost(50.0); // Satisfies @PositiveOrZero
        serviceType.setDescription("Basic oil change service");
        serviceType = serviceTypeRepository.save(serviceType);

        appointmentSlot = new AppointmentSlot();
        appointmentSlot.setStartTime(LocalDateTime.now()); // Satisfies @NotNull
        appointmentSlot.setEndTime(LocalDateTime.now().plusHours(1)); // Satisfies @NotNull
        appointmentSlot.setTechnician("Technician A"); // Satisfies @NotBlank
        appointmentSlot.setAvailable(true);
        appointmentSlot = appointmentSlotRepository.save(appointmentSlot);

        sparePart = new SparePart();
        sparePart.setPartName("Oil Filter"); // Satisfies @NotBlank
        sparePart.setPartNumber("OF123"); // Satisfies @NotBlank
        sparePart.setQuantity(10); // Satisfies @Min(0)
        sparePart.setPrice(15.0); // Satisfies @Positive
        sparePart = sparePartRepository.save(sparePart);

        bookingDTO = new ServiceBookingDTO();
        bookingDTO.setCustomerId(customer.getId());
        bookingDTO.setServiceTypeId(serviceType.getId());
        bookingDTO.setAppointmentSlotId(appointmentSlot.getId());
        bookingDTO.setStatus("PENDING");
        bookingDTO.setPriority("HIGH");
        bookingDTO.setTechnician("Technician A");
        bookingDTO.setNotes("Test booking");
        bookingDTO.setSparePartIds(Arrays.asList(sparePart.getId()));

	}
	
	
	@Test
    void testCreateBooking_Success() {
        ServiceBooking booking = serviceBookingService.createBooking(bookingDTO);
        assertNotNull(booking);
        assertEquals(customer.getId(), booking.getCustomer().getId());
        assertEquals(serviceType.getId(), booking.getServiceType().getId());
        assertEquals(appointmentSlot.getId(), booking.getAppointmentSlot().getId());
        assertEquals("PENDING", booking.getStatus());
        assertEquals("HIGH", booking.getPriority());
        assertEquals("Technician A", booking.getTechnician());
        assertEquals("Test booking", booking.getNotes());
        assertTrue(booking.getStatusHistory().get(0).contains("PENDING"));
        assertNotNull(booking.getCreatedAt());
        
        AppointmentSlot updatedSlot = appointmentSlotRepository.findById(appointmentSlot.getId()).orElseThrow();
        assertFalse(updatedSlot.isAvailable());
        SparePart updatedPart = sparePartRepository.findById(sparePart.getId()).orElseThrow();
        assertEquals(9, updatedPart.getQuantity());
	}
	
	@Test
    void testCreateBooking_CustomerNotFound() {
        bookingDTO.setCustomerId(999L);
        assertThrows(EntityNotFoundException.class, () -> serviceBookingService.createBooking(bookingDTO));
    }
	
	@Test
    void testCreateBooking_ServiceTypeNotFound() {
        bookingDTO.setServiceTypeId(999L);
        assertThrows(EntityNotFoundException.class, () -> serviceBookingService.createBooking(bookingDTO));
    }
	
	
	
	@Test
    void testGetBooking_Success() {
        ServiceBooking booking = serviceBookingService.createBooking(bookingDTO);
        ServiceBooking retrieved = serviceBookingService.getBooking(booking.getId());
        assertNotNull(retrieved);
        assertEquals(booking.getId(), retrieved.getId());
        assertEquals("PENDING", retrieved.getStatus());
    }
	
	@Test
    void testGetBooking_NotFound() {
		
        assertThrows(EntityNotFoundException.class, () -> serviceBookingService.getBooking(999L));
    }
	
	@Test
    void testSearchBookingsByCriteria() {
		serviceBookingService.createBooking(bookingDTO);
        Pageable pageable = PageRequest.of(0, 10);
        // Provide non-null parameters to satisfy the modified query
        Page<ServiceBooking> result = serviceBookingService.searchBookingsByCriteria("PENDING", "Tech", "John", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("PENDING", result.getContent().get(0).getStatus());
		
    }
	
	@Test
    void testDeleteBooking() {
        ServiceBooking booking = serviceBookingService.createBooking(bookingDTO);
        serviceBookingService.deleteBooking(booking.getId());

        assertThrows(EntityNotFoundException.class, () -> serviceBookingService.getBooking(booking.getId()));

        // Verify appointment slot is available again
        AppointmentSlot slot = appointmentSlotRepository.findById(appointmentSlot.getId()).orElseThrow();
        assertTrue(slot.isAvailable());

        // Verify spare part quantity remains unchanged (delete doesn't affect spare parts)
        SparePart updatedPart = sparePartRepository.findById(sparePart.getId()).orElseThrow();
        assertEquals(9, updatedPart.getQuantity());
    }
	
	@Test
    void testCreateBooking_AppointmentSlotNotFound() {
        bookingDTO.setAppointmentSlotId(999L);
        assertThrows(EntityNotFoundException.class, () -> serviceBookingService.createBooking(bookingDTO));
    }
	}
