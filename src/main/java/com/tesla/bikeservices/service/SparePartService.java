package com.tesla.bikeservices.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.repository.SparePartRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SparePartService {

    private final SparePartRepository sparePartRepository;

    public SparePartService(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
    }

    public SparePart createSparePart(SparePart sparePart) {
        return sparePartRepository.save(sparePart);
    }

    public SparePart getSparePart(Long id) {
        return findSparePartOrThrow(id);
    }

    public Page<SparePart> getAllSpareParts(Pageable pageable) {
        return sparePartRepository.findAll(pageable);
    }

    public SparePart updateSparePart(Long id, SparePart sparePartDetails) {
        SparePart sparePart = findSparePartOrThrow(id);
        sparePart.setPartName(sparePartDetails.getPartName());
        sparePart.setPartNumber(sparePartDetails.getPartNumber());
        sparePart.setQuantity(sparePartDetails.getQuantity());
        sparePart.setPrice(sparePartDetails.getPrice());
        return sparePartRepository.save(sparePart);
    }

    public SparePart updateStock(Long id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        SparePart sparePart = findSparePartOrThrow(id);
        sparePart.setQuantity(quantity);
        return sparePartRepository.save(sparePart);
    }

    public void deleteSparePart(Long id) {
        findSparePartOrThrow(id);
        sparePartRepository.deleteById(id);
    }

    private SparePart findSparePartOrThrow(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spare part with ID " + id + " not found"));
    }
}