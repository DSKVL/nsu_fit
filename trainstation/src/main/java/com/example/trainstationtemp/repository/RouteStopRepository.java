package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
}
