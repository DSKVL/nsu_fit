package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Department {
    @GeneratedValue
    @Id
    private Long id;
    @OneToOne
    private Worker chief;
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Brigade> brigades;
    private String name;

    public Department() {

    }
}
