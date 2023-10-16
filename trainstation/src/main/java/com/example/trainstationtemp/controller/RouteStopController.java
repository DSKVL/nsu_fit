package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.RouteStopDTO;
import com.example.trainstationtemp.repository.RouteStopRepository;
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
@RequestMapping("/route-stops")
public class RouteStopController {
    @Autowired private RouteStopRepository repository;
    @Autowired private DtoMappings mapper;

    public RouteStopController() {
    }

    @GetMapping
    public List<RouteStopDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    public RouteStopDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public RouteStopDTO post(@RequestBody RouteStopDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public RouteStopDTO put(@PathVariable Long id, @RequestBody RouteStopDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ticket not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}