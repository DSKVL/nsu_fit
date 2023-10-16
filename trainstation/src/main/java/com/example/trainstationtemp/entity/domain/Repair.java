package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor

public class Repair {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private LocomotiveInspection inspection;

    //TODO past
    private Date date;

    public Repair() {

    }
}
