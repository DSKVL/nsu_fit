package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.LocomotiveInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocomotiveInspectionRespository extends JpaRepository<LocomotiveInspection, Long> {
}
