package com.example.trainstationtemp.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
public class TicketRefund {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private Ticket ticket;
    //TODO past
    private Date date;

    public TicketRefund() {

    }
}
