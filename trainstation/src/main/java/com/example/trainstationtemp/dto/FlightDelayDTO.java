package com.example.trainstationtemp.dto;

public record FlightDelayDTO(
        Long id,
        Long flightStopId,
        String reason) {
}
