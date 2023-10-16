package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor

public class Repairman extends Worker {
    @ManyToOne
    private RepairmenBrigade repairmenBrigade;

    public Repairman() {

    }
}
