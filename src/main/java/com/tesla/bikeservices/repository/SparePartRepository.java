package com.tesla.bikeservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tesla.bikeservices.entity.SparePart;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {
}
