package com.tesla.bikeservices.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.repository.ServiceTypeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ServiceTypeService {
	
	
	@Autowired
    private ServiceTypeRepository serviceTypeRepository;
	
	public ServiceType createServiceType(ServiceType serviceType) {
        return serviceTypeRepository.save(serviceType);
    }
	
	public ServiceType getServiceType(Long id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service type not found"));
    }
	
	public List<ServiceType> getAllServiceTypes() {
        return serviceTypeRepository.findAll();
    }
	
	public ServiceType updateServiceType(Long id, ServiceType serviceTypeDetails) {
        ServiceType serviceType = getServiceType(id);
        serviceType.setName(serviceTypeDetails.getName());
        serviceType.setEstimatedDurationMinutes(serviceTypeDetails.getEstimatedDurationMinutes());
        serviceType.setCost(serviceTypeDetails.getCost());
        serviceType.setDescription(serviceTypeDetails.getDescription());
        return serviceTypeRepository.save(serviceType);
    }
	
	public void deleteServiceType(Long id) {
        serviceTypeRepository.deleteById(id);
    }

}
