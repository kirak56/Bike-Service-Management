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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.service.SparePartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spare-parts")
public class SparePartController {
	
	@Autowired
    private SparePartService sparePartService;

    @PostMapping
    public ResponseEntity<SparePart> createSparePart(@Valid @RequestBody SparePart sparePart) {
        return ResponseEntity.ok(sparePartService.createSparePart(sparePart));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SparePart> getSparePart(@PathVariable Long id) {
        return ResponseEntity.ok(sparePartService.getSparePart(id));
    }

    @GetMapping
    public ResponseEntity<List<SparePart>> getAllSpareParts() {
        return ResponseEntity.ok(sparePartService.getAllSpareParts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SparePart> updateSparePart(@PathVariable Long id, @Valid @RequestBody SparePart sparePart) {
        return ResponseEntity.ok(sparePartService.updateSparePart(id, sparePart));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<SparePart> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(sparePartService.updateStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.noContent().build();
    }
}


