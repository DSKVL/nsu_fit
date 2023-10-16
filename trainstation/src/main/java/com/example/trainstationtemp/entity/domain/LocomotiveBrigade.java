package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
public class LocomotiveBrigade {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private Locomotive locomotive;
    @OneToOne
    private Driver driver;

    public LocomotiveBrigade() {

    }
}
