package com.example.trainstationtemp.controller;


import com.example.trainstationtemp.dto.RepairmanDTO;
import com.example.trainstationtemp.repository.RepairmanRepository;
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
@RequestMapping("/repairmen")
public class RepairmanController {
    @Autowired private RepairmanRepository repository;
    @Autowired private DtoMappings mapper;

    public RepairmanController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_REPAIRMAN')")
    public List<RepairmanDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_REPAIRMAN')")
    public RepairmanDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIRMAN')")
    public RepairmanDTO post(@RequestBody RepairmanDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIRMAN')")
    public RepairmanDTO put(@PathVariable Long id, @RequestBody RepairmanDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Repairman not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIRMAN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}