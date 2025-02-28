package com.example.project.pharmacy.repository;

import com.example.project.pharmacy.domain.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
}
