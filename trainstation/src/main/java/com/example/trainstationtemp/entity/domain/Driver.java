package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
public class Driver extends Worker {
    @OneToOne
    private LocomotiveBrigade locomotiveBrigade;
    @OneToMany
    private List<MedicalExamination> medicalExaminations;
    public Driver() {
    }
}
