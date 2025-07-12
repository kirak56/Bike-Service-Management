package com.tesla.bikeservices.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class ServiceType {

	
	@GeneratedValue(strategy = GenerationType.AUTO)

	@Id
	private Long id;

	@NotBlank(message = "Service name is mandatory")
	@Size(min = 2, max = 100)
	private String name;

	@Positive(message = "Estimated duration must be positive")
	private int estimatedDurationMinutes;

	@PositiveOrZero(message = "Cost must be non-negative")
	private double cost;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

}
