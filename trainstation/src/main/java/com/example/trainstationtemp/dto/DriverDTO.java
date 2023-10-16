package com.example.trainstationtemp.dto;

import com.example.trainstationtemp.entity.domain.Gender;

import java.util.Date;

public record DriverDTO(Long id,
                        String firstName,
                        String lastName,
                        Date birthdate,
                        Gender gender,

                        Long brigadeId,
                        Date employmentDate,
                        Integer salary,
                        Boolean hasKids,
                        Integer kidsCounter,
                        Long    locomotiveBrigadeId) {
}
