package com.tesla.bikeservices.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.repository.AppointmentSlotRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AppointmentSlotService {

	@Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

	public AppointmentSlot createAppointmentSlot(AppointmentSlot slot) {
	if (slot.getStartTime().isAfter(slot.getEndTime())) {
        throw new IllegalArgumentException("Start time must be before end time");
    }
    return appointmentSlotRepository.save(slot);
}
	
	public AppointmentSlot getAppointmentSlot(Long id) {
        return appointmentSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment slot not found"));
    }
	
	public List<AppointmentSlot> getAvailableSlots(LocalDateTime start, LocalDateTime end) {
        return appointmentSlotRepository.findByStartTimeBetweenAndIsAvailableTrue(start, end);
    }
	
	public List<AppointmentSlot> getAvailableSlotsByTechnician(String technician, LocalDateTime start, LocalDateTime end) {
        return appointmentSlotRepository.findByTechnicianAndStartTimeBetween(technician, start, end);
    }
	
	public AppointmentSlot updateAppointmentSlot(Long id, AppointmentSlot slotDetails) {
        AppointmentSlot slot = getAppointmentSlot(id);
        slot.setStartTime(slotDetails.getStartTime());
        slot.setEndTime(slotDetails.getEndTime());
        slot.setTechnician(slotDetails.getTechnician());
        slot.setAvailable(slotDetails.isAvailable());
        return appointmentSlotRepository.save(slot);
    }
	
	public void deleteAppointmentSlot(Long id) {
        appointmentSlotRepository.deleteById(id);
    }
	
}
