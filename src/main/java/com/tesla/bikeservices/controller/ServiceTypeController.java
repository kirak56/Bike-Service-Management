package com.tesla.bikeservices.controller;

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
import org.springframework.web.bind.annotation.RestController;
import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.service.ServiceTypeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/service-types")
public class ServiceTypeController {
	@Autowired
    private ServiceTypeService serviceTypeService;
	
	@PostMapping
    public ResponseEntity<ServiceType> createServiceType(@Valid @RequestBody ServiceType serviceType) {
        return ResponseEntity.ok(serviceTypeService.createServiceType(serviceType));
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<ServiceType> getServiceType(@PathVariable Long id) {
        return ResponseEntity.ok(serviceTypeService.getServiceType(id));
    }
	
	@GetMapping
    public ResponseEntity<List<ServiceType>> getAllServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getAllServiceTypes());
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<ServiceType> updateServiceType(@PathVariable Long id, @Valid @RequestBody ServiceType serviceType) {
        return ResponseEntity.ok(serviceTypeService.updateServiceType(id, serviceType));
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceType(@PathVariable Long id) {
        serviceTypeService.deleteServiceType(id);
        return ResponseEntity.noContent().build();
    }

}
