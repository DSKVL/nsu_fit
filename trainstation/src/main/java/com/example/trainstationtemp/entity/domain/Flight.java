package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Route route;

    @OneToOne
    private Locomotive locomotive;

    @OrderColumn
    @ManyToMany
    private List<FlightStop> stops;

    public Flight() {

    }
}
