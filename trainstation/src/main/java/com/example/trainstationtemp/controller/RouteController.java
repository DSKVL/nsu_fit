package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.RouteDTO;
import com.example.trainstationtemp.repository.RouteRepository;
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
@RequestMapping("/routes")
public class RouteController {
    @Autowired private RouteRepository repository;
    @Autowired private DtoMappings mapper;

    public RouteController() {
    }

    @GetMapping
    public List<RouteDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    public RouteDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public RouteDTO post(@RequestBody RouteDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public RouteDTO put(@PathVariable Long id, @RequestBody RouteDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Route not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_ROUTE')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
