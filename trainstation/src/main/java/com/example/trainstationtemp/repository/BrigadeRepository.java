package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.Brigade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrigadeRepository extends JpaRepository<Brigade, Long> {
}
