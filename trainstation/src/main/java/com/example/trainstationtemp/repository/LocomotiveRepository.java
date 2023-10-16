package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.Locomotive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocomotiveRepository extends JpaRepository<Locomotive, Long> {
}
