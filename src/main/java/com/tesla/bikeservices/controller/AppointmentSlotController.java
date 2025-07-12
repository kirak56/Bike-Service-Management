package com.tesla.bikeservices.controller;

import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.service.AppointmentSlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointment-slots")
public class AppointmentSlotController{

	@Autowired
	private  AppointmentSlotService appointmentSlotService;
	
	@PostMapping
	public ResponseEntity<AppointmentSlot> createAppointmentSlot(@Valid @RequestBody AppointmentSlot slot)
	{
		return  ResponseEntity.ok(appointmentSlotService.createAppointmentSlot(slot));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AppointmentSlot> getAppointmentSlot(@PathVariable long id)
	{
		return ResponseEntity.ok(appointmentSlotService.getAppointmentSlot(id));
	}
	
	@GetMapping("/available")
	public ResponseEntity<List<AppointmentSlot>> GetAppointmentSlot(@RequestParam(required = true) LocalDateTime start , @RequestParam(required = true) LocalDateTime end){
		return ResponseEntity.ok(appointmentSlotService.getAvailableSlots(start, end));
	}
	
	@GetMapping("/available/technician")
	public ResponseEntity<List<AppointmentSlot>> GetAppointmentSlotByTechnician(@RequestParam(required = true)  String technician,@RequestParam(required = true) LocalDateTime start , @RequestParam(required = true) LocalDateTime end){
		return ResponseEntity.ok(appointmentSlotService.getAvailableSlotsByTechnician(technician,start, end));
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<AppointmentSlot> updateAppointmentSlot(@PathVariable Long id, @Valid @RequestBody AppointmentSlot slot) {
        return ResponseEntity.ok(appointmentSlotService.updateAppointmentSlot(id, slot));
        
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteAppointmentSlot(@PathVariable Long id, @Valid @RequestBody AppointmentSlot slot) {
		appointmentSlotService.deleteAppointmentSlot(id);
		return ResponseEntity.noContent().build();
        
    }
	
	
	
	
	
	
}

