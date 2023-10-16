package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
public class FlightDelay {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private FlightStop flightStop;

    @Lob
    private String reason;

    public FlightDelay() {

    }
}
