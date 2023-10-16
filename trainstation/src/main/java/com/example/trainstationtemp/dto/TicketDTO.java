package com.example.trainstationtemp.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

public record TicketDTO(
        Long id,
        Integer price,
        Long departureId,
        Long destinationId,
        Integer place,
        Long passengerId,
        Boolean lagguageIncluded
) {
}
