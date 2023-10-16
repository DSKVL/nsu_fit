package com.example.trainstationtemp.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

public record RepairDTO(
        Long id,
        Long inspectionId,
        Date date
) {
}

