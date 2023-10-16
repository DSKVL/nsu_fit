package com.example.trainstationtemp.dto;

import java.sql.Date;

public record LocomotiveDTO(
    Long id,
    Date manufacturingDate,
    Long locomotiveBrigadeId,
    Long routeId,
    Integer capacity
) {
}
