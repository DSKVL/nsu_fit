package com.example.trainstationtemp.dto;

import com.example.trainstationtemp.entity.domain.Locomotive;
import com.example.trainstationtemp.entity.domain.RepairmenBrigade;
import jakarta.persistence.ManyToOne;

import java.sql.Timestamp;
import java.util.Date;

public record FlightInspectionDTO(
        Long id,
        Long locomotiveId,
        Long repairmenBrigadeId,
        Date date,
        Long flightId
) {
}
