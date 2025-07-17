package com.tesla.bikeservices.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.response.ApiResponse;
import com.tesla.bikeservices.service.ServiceTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/service-types")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceType>> createServiceType(@Valid @RequestBody ServiceType serviceType) {
        ServiceType savedServiceType = serviceTypeService.createServiceType(serviceType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service type created successfully", savedServiceType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceType>> getServiceType(@PathVariable Long id) {
        ServiceType serviceType = serviceTypeService.getServiceType(id);
        return ResponseEntity.ok(ApiResponse.success("Service type retrieved successfully", serviceType));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceType>>> getAllServiceTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceType> serviceTypes = serviceTypeService.getAllServiceTypes(pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, serviceTypes.getTotalElements(), serviceTypes.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Service types retrieved successfully", serviceTypes, pagination));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceType>> updateServiceType(@PathVariable Long id, @Valid @RequestBody ServiceType serviceType) {
        ServiceType updatedServiceType = serviceTypeService.updateServiceType(id, serviceType);
        return ResponseEntity.ok(ApiResponse.success("Service type updated successfully", updatedServiceType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteServiceType(@PathVariable Long id) {
        serviceTypeService.deleteServiceType(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Service type deleted successfully", null));
    }
}