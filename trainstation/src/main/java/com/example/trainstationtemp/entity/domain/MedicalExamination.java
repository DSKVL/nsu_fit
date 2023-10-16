package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
public class MedicalExamination {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Driver driver;
    //TODO past constraint
    private Date date;

    public MedicalExamination() {

    }
}
