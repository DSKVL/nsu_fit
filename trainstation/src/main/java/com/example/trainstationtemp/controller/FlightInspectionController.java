package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.FlightInspectionDTO;

import com.example.trainstationtemp.repository.FlightInspectionRepository;
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
@RequestMapping("/flight-inspections")
public class FlightInspectionController {
    @Autowired private FlightInspectionRepository repository;
    @Autowired private DtoMappings mapper;

    public FlightInspectionController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_INSPECTION')")
    public List<FlightInspectionDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_INSPECTION')")
    public FlightInspectionDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_INSPECTION')")
    public FlightInspectionDTO post(@RequestBody FlightInspectionDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_INSPECTION')")
    public FlightInspectionDTO put(@PathVariable Long id, @RequestBody FlightInspectionDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Inspection not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_INSPECTION')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
