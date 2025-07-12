package com.tesla.bikeservices.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
public class SparePart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "Part name is mandatory")
	private String partName;

	@NotBlank(message = "Part number is mandatory")
	private String partNumber;

	@Min(value = 0, message = "Quantity cannot be negative")
	private int quantity;

	@Positive(message = "Price must be positive")
	private double price;

}
