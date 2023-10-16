package com.example.trainstationtemp.dto;

import java.util.Date;

public record LocomotiveInspectionDTO(
        Long id,
        Long locomotiveId,
        Long repairmenBrigadeId,
        Date date,
        Boolean needsRepair
) {
}


