package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.LocomotiveBrigade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocomotiveBrigadeRepository extends JpaRepository<LocomotiveBrigade, Long> {
}
