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

import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.response.ApiResponse;
import com.tesla.bikeservices.service.SparePartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spare-parts")
public class SparePartController {

    private final SparePartService sparePartService;

    public SparePartController(SparePartService sparePartService) {
        this.sparePartService = sparePartService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SparePart>> createSparePart(@Valid @RequestBody SparePart sparePart) {
        SparePart savedSparePart = sparePartService.createSparePart(sparePart);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Spare part created successfully", savedSparePart));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SparePart>> getSparePart(@PathVariable Long id) {
        SparePart sparePart = sparePartService.getSparePart(id);
        return ResponseEntity.ok(ApiResponse.success("Spare part retrieved successfully", sparePart));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SparePart>>> getAllSpareParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SparePart> spareParts = sparePartService.getAllSpareParts(pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(page, size, spareParts.getTotalElements(), spareParts.getTotalPages());
        return ResponseEntity.ok(ApiResponse.successPaginated("Spare parts retrieved successfully", spareParts, pagination));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SparePart>> updateSparePart(@PathVariable Long id, @Valid @RequestBody SparePart sparePart) {
        SparePart updatedSparePart = sparePartService.updateSparePart(id, sparePart);
        return ResponseEntity.ok(ApiResponse.success("Spare part updated successfully", updatedSparePart));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<SparePart>> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        SparePart updatedSparePart = sparePartService.updateStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Spare part stock updated successfully",updatedSparePart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Spare part deleted successfully", null));
    }
}