package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.FlightStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightStopRepository extends JpaRepository<FlightStop, Long> {
}
