package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Locomotive {
    @GeneratedValue
    @Id
    private Long id;
    private Date manufacturingDate;
    @OneToOne
    private LocomotiveBrigade locomotiveBrigade;
    @ManyToOne
    private Route route;
    //TODO constraint
    private Integer capacity;

    public Locomotive() {

    }
}
