package com.tesla.bikeservices.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tesla.bikeservices.entity.AppointmentSlot;
@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByStartTimeBetweenAndIsAvailableTrue(LocalDateTime start, LocalDateTime end);
    List<AppointmentSlot> findByTechnicianAndStartTimeBetween(String technician, LocalDateTime start, LocalDateTime end);
}
