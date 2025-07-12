package com.tesla.bikeservices.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.repository.SparePartRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SparePartService {

	@Autowired
    private SparePartRepository sparePartRepository;
	
	public SparePart createSparePart(SparePart sparePart) {
        return sparePartRepository.save(sparePart);
    }

    public SparePart getSparePart(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spare part not found"));
    }

    public List<SparePart> getAllSpareParts() {
        return sparePartRepository.findAll();
    }
    
    public SparePart updateSparePart(Long id, SparePart sparePartDetails) {
        SparePart sparePart = getSparePart(id);
        sparePart.setPartName(sparePartDetails.getPartName());
        sparePart.setPartNumber(sparePartDetails.getPartNumber());
        sparePart.setQuantity(sparePartDetails.getQuantity());
        sparePart.setPrice(sparePartDetails.getPrice());
        return sparePartRepository.save(sparePart);
    }

    public void deleteSparePart(Long id) {
        sparePartRepository.deleteById(id);
    }

    public SparePart updateStock(Long id, int quantity) {
        SparePart sparePart = getSparePart(id);
        sparePart.setQuantity(quantity);
        return sparePartRepository.save(sparePart);
    }
}
