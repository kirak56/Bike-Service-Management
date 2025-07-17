package com.tesla.bikeservices.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.tesla.bikeservices.entity.ServiceBooking;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ServiceBookingDTO {

    private Long id;
    private Long customerId;
    private Long serviceTypeId;
    private Long appointmentSlotId;
    @NotBlank(message = "Status is mandatory")
    private String status;
    private String priority;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String technician;
    private List<Long> sparePartIds;
    private String notes;
}