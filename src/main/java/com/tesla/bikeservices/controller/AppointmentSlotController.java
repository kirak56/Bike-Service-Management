package com.tesla.bikeservices.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

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

import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.response.ApiResponse;
import com.tesla.bikeservices.service.AppointmentSlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointment-slots")
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotController(AppointmentSlotService appointmentSlotService) {
        this.appointmentSlotService = appointmentSlotService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentSlot>> createAppointmentSlot(@Valid @RequestBody AppointmentSlot slot) {
        AppointmentSlot savedSlot = appointmentSlotService.createAppointmentSlot(slot);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment slot created successfully", savedSlot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentSlot>> getAppointmentSlot(@PathVariable long id) {
        AppointmentSlot slot = appointmentSlotService.getAppointmentSlot(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment slot retrieved successfully", slot));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AppointmentSlot>>> getAvailableSlots(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            List<AppointmentSlot> slots = appointmentSlotService.getAvailableSlots(startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success("Available slots retrieved successfully", slots));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for start or end time");
        }
    }

    @GetMapping("/available/technician")
    public ResponseEntity<ApiResponse<List<AppointmentSlot>>> getAvailableSlotsByTechnician(
            @RequestParam String technician,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            String sanitizedTechnician = technician != null ? technician.trim() : null;
            List<AppointmentSlot> slots = appointmentSlotService.getAvailableSlotsByTechnician(sanitizedTechnician, startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success("Available slots for technician retrieved successfully", slots));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for start or end time");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentSlot>> updateAppointmentSlot(@PathVariable Long id, @Valid @RequestBody AppointmentSlot slot) {
        AppointmentSlot updatedSlot = appointmentSlotService.updateAppointmentSlot(id, slot);
        return ResponseEntity.ok(ApiResponse.success("Appointment slot updated successfully", updatedSlot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointmentSlot(@PathVariable Long id) {
        appointmentSlotService.deleteAppointmentSlot(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Appointment slot deleted successfully", null));
    }
}