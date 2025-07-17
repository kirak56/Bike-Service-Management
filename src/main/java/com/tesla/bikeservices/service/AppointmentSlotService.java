package com.tesla.bikeservices.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.repository.AppointmentSlotRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
    }

    @Transactional
    public AppointmentSlot createAppointmentSlot(AppointmentSlot slot) {
        if (slot.getStartTime().isAfter(slot.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (appointmentSlotRepository.findByTechnicianAndStartTimeBetween(
                slot.getTechnician(), slot.getStartTime(), slot.getEndTime())
                .stream().anyMatch(existing -> !existing.getId().equals(slot.getId()))) {
            throw new IllegalStateException("Technician is already booked for this time");
        }
        return appointmentSlotRepository.save(slot);
    }

    public AppointmentSlot getAppointmentSlot(Long id) {
        return findAppointmentSlotOrThrow(id);
    }

    public List<AppointmentSlot> getAvailableSlots(LocalDateTime start, LocalDateTime end) {
        return appointmentSlotRepository.findByStartTimeBetweenAndIsAvailableTrue(start, end);
    }

    public List<AppointmentSlot> getAvailableSlotsByTechnician(String technician, LocalDateTime start, LocalDateTime end) {
        return appointmentSlotRepository.findByTechnicianAndStartTimeBetween(technician, start, end);
    }

    @Transactional
    public AppointmentSlot updateAppointmentSlot(Long id, AppointmentSlot slotDetails) {
        AppointmentSlot slot = findAppointmentSlotOrThrow(id);
        if (slotDetails.getStartTime().isAfter(slotDetails.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (appointmentSlotRepository.findByTechnicianAndStartTimeBetween(
                slotDetails.getTechnician(), slotDetails.getStartTime(), slotDetails.getEndTime())
                .stream().anyMatch(existing -> !existing.getId().equals(id))) {
            throw new IllegalStateException("Technician is already booked for this time");
        }
        slot.setStartTime(slotDetails.getStartTime());
        slot.setEndTime(slotDetails.getEndTime());
        slot.setTechnician(slotDetails.getTechnician());
        slot.setAvailable(slotDetails.isAvailable());
        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public void deleteAppointmentSlot(Long id) {
        findAppointmentSlotOrThrow(id);
        appointmentSlotRepository.deleteById(id);
    }

    private AppointmentSlot findAppointmentSlotOrThrow(Long id) {
        return appointmentSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment slot with ID " + id + " not found"));
    }
}