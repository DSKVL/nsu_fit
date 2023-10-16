package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Data
@Inheritance
@SuperBuilder
@AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    //TODO past
    private Date birthdate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne
    private Brigade brigade;
    //TODO past
    private Date employmentDate;
    private Integer salary;
    private Boolean hasKids;
    private Integer kidsCounter;

    public Worker() {
    }

    public Worker(String firstName, String lastName,
                  Date birthdate, Date employmentDate,
                  Gender gender, Integer salary,
                  Boolean hasKids, Integer kidsCounter) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.employmentDate = employmentDate;
        this.salary = salary;
        this.hasKids = hasKids;
        this.kidsCounter = kidsCounter;
    }

    public void assign(Worker other) {
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.birthdate = other.getBirthdate();
        this.gender = other.getGender();
        this.brigade = other.brigade;
        this.employmentDate = other.getEmploymentDate();
        this.salary = other.getSalary();
        this.hasKids = other.getHasKids();
        this.kidsCounter = other.kidsCounter;
    }
}
