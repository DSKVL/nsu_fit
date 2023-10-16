package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.RepairDTO;
import com.example.trainstationtemp.repository.RepairRepository;
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
@RequestMapping("/repairs")
public class RepairController {
    @Autowired private RepairRepository repository;
    @Autowired private DtoMappings mapper;

    public RepairController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_REPAIR')")
    public List<RepairDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_REPAIR')")
    public RepairDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIR')")
    public RepairDTO post(@RequestBody RepairDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIR')")
    public RepairDTO put(@PathVariable Long id, @RequestBody RepairDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Repair not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_REPAIR')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
