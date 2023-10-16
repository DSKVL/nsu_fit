package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.FlightDelayDTO;
import com.example.trainstationtemp.repository.FlightDelayRepository;
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
@RequestMapping("/delays")
public class FlightDelayController {
    @Autowired private FlightDelayRepository repository;
    @Autowired private DtoMappings mapper;

    public FlightDelayController() {}

    @GetMapping
    public List<FlightDelayDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    public FlightDelayDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT_DELAY')")
    public FlightDelayDTO post(@RequestBody FlightDelayDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT_DELAY')")
    public FlightDelayDTO put(@PathVariable Long id, @RequestBody FlightDelayDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Delay not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_FLIGHT_DELAY')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
