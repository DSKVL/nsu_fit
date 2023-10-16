package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor

public class FlightStop {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Route route;

    @Column(nullable = false)
    private Date plannedArrivalTime;
    private Date arrivalTime;

    @Column(nullable = false)
    private Date plannedDepartureTime;
    private Date departureTime;

    public FlightStop() {

    }
}
