package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.LocomotiveBrigadeDTO;
import com.example.trainstationtemp.dto.WorkerDTO;
import com.example.trainstationtemp.entity.domain.Brigade;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.repository.LocomotiveBrigadeRepository;
import com.example.trainstationtemp.repository.WorkerRepository;
import com.example.trainstationtemp.service.DtoMappings;
import com.example.trainstationtemp.service.TrainstationSpecification;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"${app.security.cors.origin}"})
@RequestMapping("/locomotive-brigades")
public class LocomotiveBrigadeController {
    @Autowired private LocomotiveBrigadeRepository repository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired private DtoMappings mapper;

    public LocomotiveBrigadeController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_LOCOMOTIVE_BRIGADE')")
    public List<LocomotiveBrigadeDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_LOCOMOTIVE_BRIGADE')")
    public LocomotiveBrigadeDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE_BRIGADE')")
    public LocomotiveBrigadeDTO post(@RequestBody LocomotiveBrigadeDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE_BRIGADE')")
    public LocomotiveBrigadeDTO put(@PathVariable Long id, @RequestBody LocomotiveBrigadeDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Locomotive brigade not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_LOCOMOTIVE_BRIGADE')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}