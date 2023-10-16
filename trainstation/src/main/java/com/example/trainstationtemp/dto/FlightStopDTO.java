package com.example.trainstationtemp.dto;

import java.util.Date;

public record FlightStopDTO(
        Long id,
        Long routeId,
        Date plannedArrivalTime,
        Date arrivalTime,
        Date plannedDepartureTime,
        Date departureTime
) {
}
