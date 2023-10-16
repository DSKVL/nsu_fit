package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.DepartmentDTO;
import com.example.trainstationtemp.dto.DriverDTO;
import com.example.trainstationtemp.dto.WorkerDTO;
import com.example.trainstationtemp.entity.domain.Brigade;
import com.example.trainstationtemp.entity.domain.Department;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.repository.DepartmentRepository;
import com.example.trainstationtemp.repository.DriverRepository;
import com.example.trainstationtemp.repository.WorkerRepository;
import com.example.trainstationtemp.service.DtoMappings;
import com.example.trainstationtemp.service.TrainstationSpecification;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"${app.security.cors.origin}"})
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired private DepartmentRepository repository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired private DtoMappings mapper;

    public DepartmentController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_DEPARTMENT')")
    public List<DepartmentDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_DEPARTMENT')")
    public DepartmentDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @GetMapping("/{id}/workers")
    @PreAuthorize("hasAnyAuthority('READ_WORKERS')")
    public List<WorkerDTO> get(@PathVariable Long id,
                         @RequestParam(name = "page") Integer pageId,
                         @RequestParam(name = "sort") Optional<String> sort,
                         @RequestParam(name = "filter") Optional<String> filter) {
        var department = repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Brigade not found"));

        var specification = new TrainstationSpecification<Worker>(filter) {
            @Override
            public Predicate toPredicate(Root<Worker> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Join<Worker, Brigade> brigadeJoin = root.join("brigade");
                return builder.equal(brigadeJoin.get("department"), department);
            }
        };

        var sortReq = sort.map(s -> switch (s) {
            case "salary", "kids", "brigade" -> Sort.by(Sort.Direction.ASC, s);
            default -> Sort.unsorted();
        }).orElseGet(Sort::unsorted);

        return workerRepository
                .findAll(specification, PageRequest.of(pageId, 10, sortReq))
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_DEPARTMENT')")
    public DepartmentDTO post(@RequestBody DepartmentDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_DEPARTMENT')")
    public DepartmentDTO put(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Department not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_DEPARTMENT')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}