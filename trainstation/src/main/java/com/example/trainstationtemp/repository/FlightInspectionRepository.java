package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.FlightInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightInspectionRepository extends JpaRepository<FlightInspection, Long> { }
