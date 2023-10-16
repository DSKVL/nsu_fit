package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
public class LocomotiveInspection {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Locomotive locomotive;

    @ManyToOne
    private RepairmenBrigade brigade;
    private Date date;

    //TODO constraint
    private Boolean needsRepair;

    public LocomotiveInspection() {

    }
}
