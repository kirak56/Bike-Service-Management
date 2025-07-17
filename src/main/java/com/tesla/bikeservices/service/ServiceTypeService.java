package com.tesla.bikeservices.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.repository.ServiceTypeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @Transactional
    public ServiceType createServiceType(ServiceType serviceType) {
        return serviceTypeRepository.save(serviceType);
    }

    public ServiceType getServiceType(Long id) {
        return findServiceTypeOrThrow(id);
    }

    public Page<ServiceType> getAllServiceTypes(Pageable pageable) {
        return serviceTypeRepository.findAll(pageable);
    }

    public ServiceType updateServiceType(Long id, ServiceType serviceTypeDetails) {
        ServiceType serviceType = findServiceTypeOrThrow(id);
        serviceType.setName(serviceTypeDetails.getName());
        serviceType.setEstimatedDurationMinutes(serviceTypeDetails.getEstimatedDurationMinutes());
        serviceType.setCost(serviceTypeDetails.getCost());
        serviceType.setDescription(serviceTypeDetails.getDescription());
        return serviceTypeRepository.save(serviceType);
    }

    public void deleteServiceType(Long id) {
        findServiceTypeOrThrow(id);
        serviceTypeRepository.deleteById(id);
    }

    private ServiceType findServiceTypeOrThrow(Long id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service type with ID " + id + " not found"));
    }
}