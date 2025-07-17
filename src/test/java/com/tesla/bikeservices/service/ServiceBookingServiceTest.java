package com.tesla.bikeservices.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ServiceBookingServiceTest {

    @Autowired
    private ServiceBookingRepository serviceBookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    private ServiceBookingService serviceBookingService;
    private Pageable pageable;
    private Customer customer;
    private ServiceType serviceType;
    private SparePart sparePart;
    private AppointmentSlot appointmentSlot;

    @BeforeEach
    void setUp() {
        // Initialize ServiceBookingService with autowired repositories
        serviceBookingService = new ServiceBookingService(
                serviceBookingRepository,
                customerRepository,
                serviceTypeRepository,
                sparePartRepository,
                appointmentSlotRepository
        );

        // Initialize pageable
        pageable = PageRequest.of(0, 10);

        // Initialize test data
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setBikeModel("Tesla Bike X");
        customer = customerRepository.save(customer);

        serviceType = new ServiceType();
        serviceType.setName("Oil Change");
        serviceType.setEstimatedDurationMinutes(30);
        serviceType.setCost(50.0);
        serviceType.setDescription("Standard oil change service");
        serviceType = serviceTypeRepository.save(serviceType);

        sparePart = new SparePart();
        sparePart.setPartName("Oil Filter");
        sparePart.setPartNumber("OF-123");
        sparePart.setQuantity(10);
        sparePart.setPrice(15.0);
        sparePart = sparePartRepository.save(sparePart);

        appointmentSlot = new AppointmentSlot();
        appointmentSlot.setStartTime(LocalDateTime.now().plusHours(1));
        appointmentSlot.setEndTime(LocalDateTime.now().plusHours(2));
        appointmentSlot.setTechnician("Tech1");
        appointmentSlot.setAvailable(true);
        appointmentSlot = appointmentSlotRepository.save(appointmentSlot);
    }

    @Test
    void testCreateBooking_Success() {
        // Arrange
        ServiceBookingDTO bookingDTO = new ServiceBookingDTO();
        bookingDTO.setCustomerId(customer.getId());
        bookingDTO.setServiceTypeId(serviceType.getId());
        bookingDTO.setAppointmentSlotId(appointmentSlot.getId());
        bookingDTO.setStatus("PENDING");
        bookingDTO.setPriority("HIGH");
        bookingDTO.setTechnician("Tech1");
        bookingDTO.setSparePartIds(List.of(sparePart.getId()));
        bookingDTO.setNotes("Test booking");

        // Act
        ServiceBooking savedBooking = serviceBookingService.createBooking(bookingDTO);

        // Assert
        assertNotNull(savedBooking.getId());
        assertEquals(customer.getId(), savedBooking.getCustomer().getId());
        assertEquals(serviceType.getId(), savedBooking.getServiceType().getId());
        assertEquals(appointmentSlot.getId(), savedBooking.getAppointmentSlot().getId());
        assertEquals("PENDING", savedBooking.getStatus());
        assertEquals("HIGH", savedBooking.getPriority());
        assertEquals("Tech1", savedBooking.getTechnician());
        assertEquals(1, savedBooking.getSpareParts().size());
        assertEquals("Test booking", savedBooking.getNotes());
        assertFalse(appointmentSlotRepository.findById(appointmentSlot.getId()).get().isAvailable());
        assertEquals(9, sparePartRepository.findById(sparePart.getId()).get().getQuantity());
        // Note: Logger.info("Creating booking for customer ID: {}") is called here
    }

    @Test
    void testCreateBooking_SlotNotAvailable() {
        // Arrange
        appointmentSlot.setAvailable(false);
        appointmentSlotRepository.save(appointmentSlot);

        ServiceBookingDTO bookingDTO = new ServiceBookingDTO();
        bookingDTO.setCustomerId(customer.getId());
        bookingDTO.setServiceTypeId(serviceType.getId());
        bookingDTO.setAppointmentSlotId(appointmentSlot.getId());
        bookingDTO.setStatus("PENDING");

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> serviceBookingService.createBooking(bookingDTO));
        assertEquals("Selected appointment slot is not available", exception.getMessage());
        // Note: Logger.info("Creating booking for customer ID: {}") is called here
    }

    @Test
    void testCreateBooking_SparePartOutOfStock() {
        // Arrange
        sparePart.setQuantity(0);
        sparePartRepository.save(sparePart);

        ServiceBookingDTO bookingDTO = new ServiceBookingDTO();
        bookingDTO.setCustomerId(customer.getId());
        bookingDTO.setServiceTypeId(serviceType.getId());
        bookingDTO.setAppointmentSlotId(appointmentSlot.getId());
        bookingDTO.setStatus("PENDING");
        bookingDTO.setSparePartIds(List.of(sparePart.getId()));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> serviceBookingService.createBooking(bookingDTO));
        assertEquals("Spare part out of stock: Oil Filter", exception.getMessage());
        // Note: Logger.info("Creating booking for customer ID: {}") is called here
    }

    @Test
    void testGetBooking_Success() {
        // Arrange
        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus("PENDING");
        booking.setPriority("HIGH");
        booking.setTechnician("Tech1");
        ServiceBooking savedBooking = serviceBookingRepository.save(booking);

        // Act
        ServiceBooking foundBooking = serviceBookingService.getBooking(savedBooking.getId());

        // Assert
        assertNotNull(foundBooking);
        assertEquals(savedBooking.getId(), foundBooking.getId());
        assertEquals(customer.getId(), foundBooking.getCustomer().getId());
        assertEquals(serviceType.getId(), foundBooking.getServiceType().getId());
        // Note: Logger.debug("Fetching booking with ID: {}") is called here
    }

    @Test
    void testGetBooking_NotFound() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> serviceBookingService.getBooking(999L));
        assertEquals("Booking with ID 999 not found", exception.getMessage());
        // Note: Logger.debug("Fetching booking with ID: {}") is called here
    }

	/*
	 * @Test void testGetAllBookings_Success() { // Arrange ServiceBooking booking1
	 * = new ServiceBooking(); booking1.setCustomer(customer);
	 * booking1.setServiceType(serviceType);
	 * booking1.setAppointmentSlot(appointmentSlot);
	 * booking1.setStatus(ServiceBooking.BookingStatus.SCHEDULED);
	 * booking1.setTechnician("Tech1"); ServiceBooking savedBooking1 =
	 * serviceBookingRepository.save(booking1);
	 * 
	 * AppointmentSlot slot2 = new AppointmentSlot();
	 * slot2.setStartTime(LocalDateTime.now().plusHours(3));
	 * slot2.setEndTime(LocalDateTime.now().plusHours(4));
	 * slot2.setTechnician("Tech2"); slot2.setAvailable(false); slot2 =
	 * appointmentSlotRepository.save(slot2);
	 * 
	 * ServiceBooking booking2 = new ServiceBooking();
	 * booking2.setCustomer(customer); booking2.setServiceType(serviceType);
	 * booking2.setAppointmentSlot(slot2);
	 * booking2.setStatus(ServiceBooking.BookingStatus.SCHEDULED);
	 * booking2.setTechnician("Tech2"); ServiceBooking savedBooking2 =
	 * serviceBookingRepository.save(booking2);
	 * 
	 * // Act Page<ServiceBooking> result =
	 * serviceBookingService.getAllBookings(pageable);
	 * 
	 * // Assert assertNotNull(result); assertEquals(2, result.getTotalElements());
	 * assertEquals(1, result.getTotalPages());
	 * assertTrue(result.getContent().stream().anyMatch(b ->
	 * b.getTechnician().equals(savedBooking1.getTechnician())));
	 * assertTrue(result.getContent().stream().anyMatch(b ->
	 * b.getTechnician().equals(savedBooking2.getTechnician()))); // Note:
	 * Logger.debug("Fetching all bookings with pagination: page={}, size={}") is
	 * called here }
	 */
    @Test
    void testGetAllBookings_EmptyPage() {
        // Act
        Page<ServiceBooking> result = serviceBookingService.getAllBookings(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        // Note: Logger.debug("Fetching all bookings with pagination: page={}, size={}") is called here
    }
    @Test
    void testGetBookingsByCustomer_EmptyResult() {
        // Act
        List<ServiceBooking> result = serviceBookingService.getBookingsByCustomer(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        // Note: Logger.debug("Fetching bookings for customer ID: {}") is called here
    }

	/*
	 * @Test void testGetBookingsByStatus_EmptyResult() { // Act
	 * List<ServiceBooking> result =
	 * serviceBookingService.getBookingsByStatus("COMPLETED");
	 * 
	 * // Assert assertNotNull(result); assertEquals(0, result.size()); // Note:
	 * Logger.debug("Fetching bookings with status: {}") is called here }
	 */

    @Test
    void testGetBookingsByStatus_InvalidStatus() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> serviceBookingService.getBookingsByStatus("INVALID"));
        assertEquals("Invalid status: INVALID", exception.getMessage());
        // Note: Logger.debug("Fetching bookings with status: {}") is called here
    }

	/*
	 * @Test void testUpdateBooking_Success() { // Arrange ServiceBooking booking =
	 * new ServiceBooking(); booking.setCustomer(customer);
	 * booking.setServiceType(serviceType);
	 * booking.setAppointmentSlot(appointmentSlot);
	 * booking.setStatus(ServiceBooking.BookingStatus.SCHEDULED);
	 * booking.setTechnician("Tech1"); ServiceBooking savedBooking =
	 * serviceBookingRepository.save(booking);
	 * 
	 * AppointmentSlot newSlot = new AppointmentSlot();
	 * newSlot.setStartTime(LocalDateTime.now().plusHours(5));
	 * newSlot.setEndTime(LocalDateTime.now().plusHours(6));
	 * newSlot.setTechnician("Tech2"); newSlot.setAvailable(true); newSlot =
	 * appointmentSlotRepository.save(newSlot);
	 * 
	 * ServiceBookingDTO updatedDTO = new ServiceBookingDTO();
	 * updatedDTO.setCustomerId(customer.getId());
	 * updatedDTO.setServiceTypeId(serviceType.getId());
	 * updatedDTO.setAppointmentSlotId(newSlot.getId());
	 * updatedDTO.setStatus(ServiceBooking.BookingStatus.IN_PROGRESS);
	 * updatedDTO.setPriority(ServiceBooking.BookingPriority.HIGH);
	 * updatedDTO.setTechnician("Tech2"); updatedDTO.setNotes("Updated booking");
	 * 
	 * // Act ServiceBooking updatedBooking =
	 * serviceBookingService.updateBooking(savedBooking.getId(), updatedDTO);
	 * 
	 * // Assert assertNotNull(updatedBooking); assertEquals(savedBooking.getId(),
	 * updatedBooking.getId());
	 * assertEquals(ServiceBooking.BookingStatus.IN_PROGRESS,
	 * updatedBooking.getStatus());
	 * assertEquals(ServiceBooking.BookingPriority.HIGH,
	 * updatedBooking.getPriority()); assertEquals("Tech2",
	 * updatedBooking.getTechnician()); assertEquals("Updated booking",
	 * updatedBooking.getNotes());
	 * assertTrue(appointmentSlotRepository.findById(appointmentSlot.getId()).get().
	 * isAvailable());
	 * assertFalse(appointmentSlotRepository.findById(newSlot.getId()).get().
	 * isAvailable()); // Note: Logger.info("Updating booking with ID: {}") is
	 * called here }
	 */
    @Test
    void testUpdateBooking_NotFound() {
        // Arrange
        ServiceBookingDTO updatedDTO = new ServiceBookingDTO();
        updatedDTO.setCustomerId(customer.getId());
        updatedDTO.setServiceTypeId(serviceType.getId());
        updatedDTO.setAppointmentSlotId(appointmentSlot.getId());
        updatedDTO.setStatus("INPROGRESS");

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> serviceBookingService.updateBooking(999L, updatedDTO));
        assertEquals("Booking with ID 999 not found", exception.getMessage());
        // Note: Logger.info("Updating booking with ID: {}") is called here
    }

    @Test
    void testUpdateBooking_SlotNotAvailable() {
        // Arrange
        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus("PENDING");
        ServiceBooking savedBooking = serviceBookingRepository.save(booking);

        AppointmentSlot newSlot = new AppointmentSlot();
        newSlot.setStartTime(LocalDateTime.now().plusHours(5));
        newSlot.setEndTime(LocalDateTime.now().plusHours(6));
        newSlot.setTechnician("Tech2");
        newSlot.setAvailable(false);
        newSlot = appointmentSlotRepository.save(newSlot);

        ServiceBookingDTO updatedDTO = new ServiceBookingDTO();
        updatedDTO.setCustomerId(customer.getId());
        updatedDTO.setServiceTypeId(serviceType.getId());
        updatedDTO.setAppointmentSlotId(newSlot.getId());
        updatedDTO.setStatus("INPROGRESS");

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> serviceBookingService.updateBooking(savedBooking.getId(), updatedDTO));
        assertEquals("Selected appointment slot is not available", exception.getMessage());
        // Note: Logger.info("Updating booking with ID: {}") is called here
    }

    @Test
    void testDeleteBooking_Success() {
        // Arrange
        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus("PENDING");
        ServiceBooking savedBooking = serviceBookingRepository.save(booking);

        // Act
        serviceBookingService.deleteBooking(savedBooking.getId());

        // Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> serviceBookingService.getBooking(savedBooking.getId()));
        assertEquals("Booking with ID " + savedBooking.getId() + " not found", exception.getMessage());
        assertTrue(appointmentSlotRepository.findById(appointmentSlot.getId()).get().isAvailable());
        // Note: Logger.info("Deleting booking with ID: {}") is called here
    }

    @Test
    void testDeleteBooking_NotFound() {
        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> serviceBookingService.deleteBooking(999L));
        assertEquals("Booking with ID 999 not found", exception.getMessage());
        // Note: Logger.info("Deleting booking with ID: {}") is called here
    }

	/*
	 * @Test void testSearchBookingsByCriteria_Success() { // Arrange ServiceBooking
	 * booking = new ServiceBooking(); booking.setCustomer(customer);
	 * booking.setServiceType(serviceType);
	 * booking.setAppointmentSlot(appointmentSlot);
	 * booking.setStatus(ServiceBooking.BookingStatus.SCHEDULED);
	 * booking.setTechnician("Tech1"); ServiceBooking savedBooking =
	 * serviceBookingRepository.save(booking);
	 * 
	 * // Act Page<ServiceBooking> result =
	 * serviceBookingService.searchBookingsByCriteria( "SCHEDULED", "Tech", "John",
	 * pageable);
	 * 
	 * // Assert assertNotNull(result); assertEquals(1, result.getTotalElements());
	 * assertTrue(result.getContent().stream().anyMatch(b ->
	 * b.getTechnician().equals(savedBooking.getTechnician())));
	 * assertEquals("John Doe", result.getContent().get(0).getCustomer().getName());
	 * // Note: Logger.
	 * debug("Searching bookings with status={}, technicianPrefix={}, customerPrefix={}"
	 * ) is called here }
	 */
    @Test
    void testSearchBookingsByCriteria_NullParameters() {
        // Arrange
        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus("PENDING");
        booking.setTechnician("Tech1");
        serviceBookingRepository.save(booking);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> serviceBookingService.searchBookingsByCriteria(null, "Tech", "John", pageable));
        assertEquals("All search criteria (status, technicianPrefix, customerPrefix) must be non-null", exception.getMessage());
        // Note: Logger.debug("Searching bookings with status={}, technicianPrefix={}, customerPrefix={}") is called here
    }

	/*
	 * @Test void testSearchBookingsByCriteria_EmptyResult() { // Arrange
	 * ServiceBooking booking = new ServiceBooking(); booking.setCustomer(customer);
	 * booking.setServiceType(serviceType);
	 * booking.setAppointmentSlot(appointmentSlot);
	 * booking.setStatus(ServiceBooking.BookingStatus.SCHEDULED);
	 * booking.setTechnician("Tech1"); serviceBookingRepository.save(booking);
	 * 
	 * // Act Page<ServiceBooking> result =
	 * serviceBookingService.searchBookingsByCriteria( "COMPLETED", "Tech", "John",
	 * pageable);
	 * 
	 * // Assert assertNotNull(result); assertEquals(0, result.getTotalElements());
	 * assertEquals(0, result.getContent().size()); // Note: Logger.
	 * debug("Searching bookings with status={}, technicianPrefix={}, customerPrefix={}"
	 * ) is called here }
	 */
}