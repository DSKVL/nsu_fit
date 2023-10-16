package com.example.trainstationtemp.dto;

import com.example.trainstationtemp.entity.domain.Gender;

import java.util.Date;

public record PassengerDTO (
        Long id,
        String firstName,
        String lastName,
        Date birthdate,
        Gender gender
) {
}