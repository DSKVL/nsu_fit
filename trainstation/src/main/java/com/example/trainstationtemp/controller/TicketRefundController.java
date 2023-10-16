package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.TicketRefundDTO;
import com.example.trainstationtemp.repository.TicketRefundRepository;
import com.example.trainstationtemp.service.DtoMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = {"${app.security.cors.origin}"})
@RequestMapping("/ticket-refunds")
public class TicketRefundController {
    @Autowired
    private TicketRefundRepository repository;
    @Autowired private DtoMappings mapper;

    public TicketRefundController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_TICKET_REFUND')")
    public List<TicketRefundDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_TICKET_REFUND')")
    public TicketRefundDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_TICKET_REFUND')")
    public TicketRefundDTO post(@RequestBody TicketRefundDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_TICKET_REFUND')")
    public TicketRefundDTO put(@PathVariable Long id, @RequestBody TicketRefundDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ticket refund not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_TICKET_REFUND')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
