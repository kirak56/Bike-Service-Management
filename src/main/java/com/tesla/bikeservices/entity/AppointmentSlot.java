package com.tesla.bikeservices.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class AppointmentSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "Start time is mandatory")
	private LocalDateTime startTime;

	@NotNull(message = "End time is mandatory")
	private LocalDateTime endTime;

	@NotBlank(message = "Technician is mandatory")
	private String technician;

	private boolean isAvailable = true;

	@OneToOne(mappedBy = "appointmentSlot")
	private ServiceBooking serviceBooking;
}
