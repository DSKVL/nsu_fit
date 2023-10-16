package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.FlightDTO;
import com.example.trainstationtemp.repository.FlightRepository;
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
@RequestMapping("/flights")
public class FlightController {
    @Autowired private FlightRepository repository;
    @Autowired private DtoMappings mapper;

    public FlightController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_FLIGHT')")
    public List<FlightDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_FLIGHT')")
    public FlightDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT')")
    public FlightDTO post(@RequestBody FlightDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT')")
    public FlightDTO put(@PathVariable Long id, @RequestBody FlightDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Flight not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
