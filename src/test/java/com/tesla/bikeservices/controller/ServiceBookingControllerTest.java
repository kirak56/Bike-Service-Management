package com.tesla.bikeservices.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest( ServiceBookingController.class)
class ServiceBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceBookingService serviceBookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceBookingDTO bookingDTO;
    private ServiceBooking savedBooking;
    private Customer customer;
    private ServiceType serviceType;
    private AppointmentSlot appointmentSlot;
    private SparePart sparePart;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");
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

        savedBooking = new ServiceBooking();
        savedBooking.setId(1L);
        savedBooking.setCustomer(customer);
        savedBooking.setServiceType(serviceType);
        savedBooking.setAppointmentSlot(appointmentSlot);
        savedBooking.setStatus("PENDING");
        savedBooking.setPriority("HIGH");
        savedBooking.setTechnician("Technician A");
        savedBooking.setNotes("Test booking");
        savedBooking.setSpareParts(Arrays.asList(sparePart));
        savedBooking.setStatusHistory(Arrays.asList("Created: PENDING at " + LocalDateTime.now()));
        savedBooking.setCreatedAt(LocalDateTime.now());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(serviceBookingService.createBooking(any(ServiceBookingDTO.class))).thenReturn(savedBooking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedBooking.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.serviceType.id").value(serviceType.getId()))
                .andExpect(jsonPath("$.appointmentSlot.id").value(appointmentSlot.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.technician").value("Technician A"))
                .andExpect(jsonPath("$.notes").value("Test booking"))
                .andExpect(jsonPath("$.spareParts[0].id").value(sparePart.getId()))
                .andExpect(jsonPath("$.statusHistory[0]").value(savedBooking.getStatusHistory().get(0)))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testCreateBooking_SlotNotAvailable() throws Exception {
        when(serviceBookingService.createBooking(any(ServiceBookingDTO.class)))
                .thenThrow(new IllegalStateException("Selected appointment slot is not available"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Selected appointment slot is not available"));
    }

    @Test
    void testCreateBooking_CustomerNotFound() throws Exception {
        when(serviceBookingService.createBooking(any(ServiceBookingDTO.class)))
                .thenThrow(new EntityNotFoundException("Customer with ID 1 not found"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with ID 1 not found"));
    }

    @Test
    void testCreateBooking_InvalidDTO() throws Exception {
        ServiceBookingDTO invalidDTO = new ServiceBookingDTO();
        invalidDTO.setCustomerId(null); // Missing required field

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBooking_Success() throws Exception {
        when(serviceBookingService.getBooking(eq(1L))).thenReturn(savedBooking);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBooking.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.serviceType.id").value(serviceType.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetBooking_NotFound() throws Exception {
        when(serviceBookingService.getBooking(eq(999L)))
                .thenThrow(new EntityNotFoundException("Booking with ID 999 not found"));

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking with ID 999 not found"));
    }

    @Test
    void testGetAllBookings_Success() throws Exception {
        Page<ServiceBooking> page = new PageImpl<>(Arrays.asList(savedBooking), pageable, 1);
        when(serviceBookingService.getAllBookings(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/bookings")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(savedBooking.getId()))
                .andExpect(jsonPath("$.content[0].technician").value("Technician A"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetAllBookings_EmptyPage() throws Exception {
        Page<ServiceBooking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(serviceBookingService.getAllBookings(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/bookings")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void testGetBookingsByCustomer_Success() throws Exception {
        when(serviceBookingService.getBookingsByCustomer(eq(1L)))
                .thenReturn(Arrays.asList(savedBooking));

        mockMvc.perform(get("/api/bookings/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedBooking.getId()))
                .andExpect(jsonPath("$[0].customer.id").value(customer.getId()));
    }

    @Test
    void testGetBookingsByCustomer_EmptyResult() throws Exception {
        when(serviceBookingService.getBookingsByCustomer(eq(999L)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bookings/customer/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetBookingsByStatus_Success() throws Exception {
        when(serviceBookingService.getBookingsByStatus(eq("PENDING")))
                .thenReturn(Arrays.asList(savedBooking));

        mockMvc.perform(get("/api/bookings/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedBooking.getId()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testGetBookingsByStatus_InvalidStatus() throws Exception {
        when(serviceBookingService.getBookingsByStatus(eq("INVALID")))
                .thenThrow(new IllegalArgumentException("Invalid status: INVALID"));

        mockMvc.perform(get("/api/bookings/status/INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status: INVALID"));
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        ServiceBooking updatedBooking = new ServiceBooking();
        updatedBooking.setId(1L);
        updatedBooking.setCustomer(customer);
        updatedBooking.setServiceType(serviceType);
        updatedBooking.setAppointmentSlot(appointmentSlot);
        updatedBooking.setStatus("PENDING");
        updatedBooking.setPriority("HIGH");
        updatedBooking.setTechnician("Technician B");
        updatedBooking.setNotes("Updated booking");
        updatedBooking.setSpareParts(Arrays.asList(sparePart));
        updatedBooking.setStatusHistory(Arrays.asList("Updated: IN_PROGRESS at " + LocalDateTime.now()));
        updatedBooking.setCreatedAt(LocalDateTime.now());

        when(serviceBookingService.updateBooking(eq(1L), any(ServiceBookingDTO.class)))
                .thenReturn(updatedBooking);

        ServiceBookingDTO updatedDTO = new ServiceBookingDTO();
        updatedDTO.setCustomerId(customer.getId());
        updatedDTO.setServiceTypeId(serviceType.getId());
        updatedDTO.setAppointmentSlotId(appointmentSlot.getId());
        updatedDTO.setStatus("PENDING");
        updatedDTO.setPriority("HIGH");
        updatedDTO.setTechnician("Technician B");
        updatedDTO.setNotes("Updated booking");
        updatedDTO.setSparePartIds(Arrays.asList(sparePart.getId()));

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedBooking.getId()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.technician").value("Technician B"))
                .andExpect(jsonPath("$.notes").value("Updated booking"));
    }

    @Test
    void testUpdateBooking_NotFound() throws Exception {
        when(serviceBookingService.updateBooking(eq(999L), any(ServiceBookingDTO.class)))
                .thenThrow(new EntityNotFoundException("Booking with ID 999 not found"));

        mockMvc.perform(put("/api/bookings/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking with ID 999 not found"));
    }

    @Test
    void testUpdateBooking_SlotNotAvailable() throws Exception {
        when(serviceBookingService.updateBooking(eq(1L), any(ServiceBookingDTO.class)))
                .thenThrow(new IllegalStateException("Selected appointment slot is not available"));

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Selected appointment slot is not available"));
    }

    @Test
    void testDeleteBooking_Success() throws Exception {
        doNothing().when(serviceBookingService).deleteBooking(eq(1L));

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());
        verify(serviceBookingService, times(1)).deleteBooking(eq(1L));
    }

    @Test
    void testDeleteBooking_NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Booking with ID 999 not found"))
                .when(serviceBookingService).deleteBooking(eq(999L));

        mockMvc.perform(delete("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking with ID 999 not found"));
    }

    @Test
    void testSearchBookingsByCriteria_Success() throws Exception {
        Page<ServiceBooking> page = new PageImpl<>(Arrays.asList(savedBooking), pageable, 1);
        when(serviceBookingService.searchBookingsByCriteria(
                eq("PENDING"), eq("Tech"), eq("John"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings/search")
                .param("status", "PENDING")
                .param("technicianPrefix", "Tech")
                .param("customerPrefix", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(savedBooking.getId()))
                .andExpect(jsonPath("$.content[0].technician").value("Technician A"))
                .andExpect(jsonPath("$.content[0].customer.name").value("John Doe"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testSearchBookingsByCriteria_NullParameters() throws Exception {
        when(serviceBookingService.searchBookingsByCriteria(
                isNull(), eq("Tech"), eq("John"), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("All search criteria (status, technicianPrefix, customerPrefix) must be non-null"));

        mockMvc.perform(get("/api/bookings/search")
                .param("technicianPrefix", "Tech")
                .param("customerPrefix", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("All search criteria (status, technicianPrefix, customerPrefix) must be non-null"));
    }

    @Test
    void testSearchBookingsByCriteria_EmptyResult() throws Exception {
        Page<ServiceBooking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(serviceBookingService.searchBookingsByCriteria(
                eq("COMPLETED"), eq("Tech"), eq("John"), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/bookings/search")
                .param("status", "COMPLETED")
                .param("technicianPrefix", "Tech")
                .param("customerPrefix", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}