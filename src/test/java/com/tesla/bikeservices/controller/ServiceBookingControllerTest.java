package com.tesla.bikeservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesla.bikeservices.dto.ServiceBookingDTO;
import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.entity.ServiceBooking;
import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.service.ServiceBookingService;

@WebMvcTest(ServiceBookingController.class)
class ServiceBookingControllerTest {


	@Autowired
    private MockMvc mockMvc;

	@MockitoBean
    private ServiceBookingService serviceBookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceBookingDTO bookingDTO;
    private ServiceBooking booking;
    private Customer customer;
    private ServiceType serviceType;
    private AppointmentSlot appointmentSlot;
    private SparePart sparePart;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890"); // Satisfies @Pattern(regexp="^\\+?[1-9]\\d{1,14}$")
        customer.setBikeModel("Tesla Bike X1");

        serviceType = new ServiceType();
        serviceType.setId(1L);
        serviceType.setName("Oil Change");
        serviceType.setEstimatedDurationMinutes(60);
        serviceType.setCost(50.0);
        serviceType.setDescription("Basic oil change service");

        appointmentSlot = new AppointmentSlot();
        appointmentSlot.setId(1L);
        appointmentSlot.setStartTime(LocalDateTime.now());
        appointmentSlot.setEndTime(LocalDateTime.now().plusHours(1));
        appointmentSlot.setTechnician("Technician A");
        appointmentSlot.setAvailable(false);

        sparePart = new SparePart();
        sparePart.setId(1L);
        sparePart.setPartName("Oil Filter");
        sparePart.setPartNumber("OF123");
        sparePart.setQuantity(9);
        sparePart.setPrice(15.0);

        bookingDTO = new ServiceBookingDTO();
        bookingDTO.setCustomerId(customer.getId());
        bookingDTO.setServiceTypeId(serviceType.getId());
        bookingDTO.setAppointmentSlotId(appointmentSlot.getId());
        bookingDTO.setStatus("PENDING");
        bookingDTO.setPriority("HIGH");
        bookingDTO.setTechnician("Technician A");
        bookingDTO.setNotes("Test booking");
        bookingDTO.setSparePartIds(Arrays.asList(sparePart.getId()));

        booking = new ServiceBooking();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus("PENDING");
        booking.setPriority("HIGH");
        booking.setTechnician("Technician A");
        booking.setNotes("Test booking");
        booking.setSpareParts(Arrays.asList(sparePart));
        booking.setStatusHistory(Arrays.asList("Created: PENDING at " + LocalDateTime.now()));
        booking.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testCreateBooking_Success() throws Exception {
        when(serviceBookingService.createBooking(any(ServiceBookingDTO.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.serviceType.id").value(serviceType.getId()))
                .andExpect(jsonPath("$.appointmentSlot.id").value(appointmentSlot.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.technician").value("Technician A"))
                .andExpect(jsonPath("$.notes").value("Test booking"))
                .andExpect(jsonPath("$.spareParts[0].id").value(sparePart.getId()))
                .andExpect(jsonPath("$.statusHistory[0]").value(booking.getStatusHistory().get(0)))
                .andExpect(jsonPath("$.createdAt").exists());

    }
}
