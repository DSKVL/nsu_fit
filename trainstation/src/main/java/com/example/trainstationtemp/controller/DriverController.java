package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.DriverDTO;
import com.example.trainstationtemp.entity.domain.Driver;
import com.example.trainstationtemp.entity.domain.MedicalExamination;
import com.example.trainstationtemp.repository.DriverRepository;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"${app.security.cors.origin}"})
@RequestMapping("/drivers")
public class DriverController {
    @Autowired private DriverRepository repository;
    @Autowired private DtoMappings mapper;
    public DriverController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_DRIVER')")
    public List<DriverDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_DRIVER')")
    public DriverDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @GetMapping("/workers/examined")
    @PreAuthorize("hasAnyAuthority('READ_DRIVER')")
    public List<DriverDTO> get(@RequestParam(name = "page") Integer pageId,
                               @RequestParam(name = "sort") Optional<String> sort,
                               @RequestParam(name = "filter") Optional<String> filter) {
        var specification = new TrainstationSpecification<Driver>(filter) {
            private final static Long MILLIS_IN_YEAR = 1000L *60L*60L*24L*365L;
            @Override
            public Predicate toPredicate(Root<Driver> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Join<Driver, MedicalExamination> medicalExaminationJoin = root.join("medicalExaminations");
                return builder.between(medicalExaminationJoin.get("date"),
                        new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis() - MILLIS_IN_YEAR));
            }
        };

        var sortReq = sort.map(s -> switch (s) {
            case "salary", "kids", "brigade" -> Sort.by(Sort.Direction.ASC, s);
            default -> Sort.unsorted();
        }).orElseGet(Sort::unsorted);

        return repository
                .findAll(specification, PageRequest.of(pageId, 10, sortReq))
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_DRIVER')")
    public DriverDTO post(@RequestBody DriverDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_DRIVER')")
    public DriverDTO put(@PathVariable Long id, @RequestBody DriverDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_DRIVER')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
