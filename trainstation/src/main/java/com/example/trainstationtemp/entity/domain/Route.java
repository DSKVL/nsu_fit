package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor

public class Route {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private RouteType type;
    @Enumerated(EnumType.STRING)
    private RouteDestinationType destinationType;
    @OrderColumn
    @ManyToMany
    private List<RouteStop> stops;
    @OneToMany
    private List<Locomotive> locomotives;

    public Route() {

    }
}
