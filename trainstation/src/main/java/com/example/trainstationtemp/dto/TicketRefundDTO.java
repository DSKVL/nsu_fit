package com.example.trainstationtemp.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

public record TicketRefundDTO(
        Long id,
        Long ticketId,
        Date date
) {
}
