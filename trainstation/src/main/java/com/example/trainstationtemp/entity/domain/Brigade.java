package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
public class Brigade {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private Worker brigadier;
    @ManyToOne
    private Department department;
    @OneToMany(mappedBy = "brigade", fetch = FetchType.LAZY)
    private List<Worker> workers;

    public Brigade() {
    }
}
