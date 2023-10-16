package com.example.trainstationtemp.dto;

import java.util.Date;

public record MedicalExaminationDTO (
        Long id,
        Long driverId,
        Date date
) {
}

