package com.example.trainstationtemp.dto;

import com.example.trainstationtemp.entity.domain.Gender;
import com.example.trainstationtemp.entity.domain.Worker;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Date;

public record RepairmanDTO(
        Long id,
        String firstName,
        String lastName,
        Date birthdate,
        Gender gender,

        Long brigadeId,
        Date employmentDate,
        Integer salary,
        Boolean hasKids,
        Integer kidsCounter,
        Long repairmenBrigadeId
) {
}
