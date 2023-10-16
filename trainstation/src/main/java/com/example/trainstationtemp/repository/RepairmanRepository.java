package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.Repairman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairmanRepository extends JpaRepository<Repairman, Long> {
}
