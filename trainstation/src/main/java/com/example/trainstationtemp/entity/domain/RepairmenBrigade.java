package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor

public class RepairmenBrigade {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    private List<Repairman> repairmen;

    public RepairmenBrigade() {

    }
}
