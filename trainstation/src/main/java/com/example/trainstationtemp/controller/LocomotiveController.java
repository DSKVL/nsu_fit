package com.example.trainstationtemp.controller;


import com.example.trainstationtemp.dto.LocomotiveDTO;
import com.example.trainstationtemp.repository.LocomotiveRepository;
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
@RequestMapping("/locomotives")
public class LocomotiveController {
    @Autowired private LocomotiveRepository repository;
    @Autowired private DtoMappings mapper;

    public LocomotiveController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_LOCOMOTIVE')")
    public List<LocomotiveDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_LOCOMOTIVE')")
    public LocomotiveDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }



    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE')")
    public LocomotiveDTO post(@RequestBody LocomotiveDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE')")
    public LocomotiveDTO put(@PathVariable Long id, @RequestBody LocomotiveDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Locomotive not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}