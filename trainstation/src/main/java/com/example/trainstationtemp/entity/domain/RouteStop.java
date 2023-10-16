package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor

public class RouteStop {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToMany
    private List<Route> routes;
    private String stationName;

    public RouteStop() {

    }
}
