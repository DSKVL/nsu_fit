package com.example.trainstationtemp.dto;

import com.example.trainstationtemp.entity.domain.RouteDestinationType;
import com.example.trainstationtemp.entity.domain.RouteType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public record RouteDTO(
        Long id,
        RouteType type,
        RouteDestinationType destinationType
) {
}
