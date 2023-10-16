package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Passenger {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    //TODO past constraint
    private Date birthdate;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Passenger() {

    }
}
