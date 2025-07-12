package com.tesla.bikeservices.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class ServiceBooking {
	
	@GeneratedValue(strategy = GenerationType.AUTO)

	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "service_type_id")
	private ServiceType serviceType;

	@OneToOne
	@JoinColumn(name = "appointment_slot_id")
	private AppointmentSlot appointmentSlot;

	@NotBlank(message = "Status is mandatory")
	private String status;

	private String priority; // LOW, personally, I would use an enum for this

	private LocalDateTime actualStartTime;

	private LocalDateTime actualEndTime;

	private String technician;

	@ElementCollection
	private List<String> statusHistory = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "booking_spare_parts", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "spare_part_id"))

	private List<SparePart> spareParts = new ArrayList<>();

	private String notes;
	private LocalDateTime createdAt = LocalDateTime.now();
}
