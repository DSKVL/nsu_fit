package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.FlightDelay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightDelayRepository  extends JpaRepository<FlightDelay, Long> {
}
