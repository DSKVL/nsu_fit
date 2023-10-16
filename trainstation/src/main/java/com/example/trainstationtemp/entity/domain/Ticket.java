package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor

public class Ticket {
    @Id
    @GeneratedValue
    private Long id;
    private Integer price;

    @ManyToOne
    private FlightStop departure;
    @ManyToOne
    private FlightStop destination;

    //TODO constraint unique for flight
    private Integer place;

    @ManyToOne
    private Passenger passenger;
    Boolean lagguageIncluded;

    public Ticket() {

    }
}
