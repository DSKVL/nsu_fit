package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.BrigadeDTO;
import com.example.trainstationtemp.dto.WorkerDTO;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.repository.BrigadeRepository;
import com.example.trainstationtemp.repository.WorkerRepository;
import com.example.trainstationtemp.service.DtoMappings;
import com.example.trainstationtemp.service.TrainstationSpecification;
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
@RequestMapping("/brigades")
public class BrigadeController {
    @Autowired private BrigadeRepository repository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired private DtoMappings mapper;

    public BrigadeController() {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_BRIGADES')")
    public List<BrigadeDTO> get(@RequestParam(name = "page") Integer pageId) {
        var page = PageRequest.of(pageId, 10);
        return repository.findAll(page).map(mapper::mapToDto).toList();
    }

    @PreAuthorize("hasAuthority('READ_WORKER')")
    @GetMapping("{id}/workers/")
    public List<WorkerDTO> get(@PathVariable Long id,
                               @RequestParam(name = "page") Integer pageId,
                               @RequestParam(name = "sort") Optional<String> sort,
                               @RequestParam(name = "filter") Optional<String> filter) {
        var brigade = repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Brigade not found"));

        var specification = new TrainstationSpecification<Worker>(filter);
        var sortReq = sort.map(s -> switch (s) {
            case "salary", "kids", "brigade" -> Sort.by(Sort.Direction.ASC, s);
            default -> Sort.unsorted();
        }).orElseGet(Sort::unsorted);

        return workerRepository
                .findAllByBrigade(brigade, specification, PageRequest.of(pageId, 10, sortReq))
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_BRIGADES')")
    public BrigadeDTO get(@PathVariable Long id) {
        return mapper.mapToDto(repository.findById(id).orElseThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_BRIGADES')")
    public BrigadeDTO post(@RequestBody BrigadeDTO dto) {
        return mapper.mapToDto(repository.save(mapper.mapToEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_BRIGADES')")
    public BrigadeDTO put(@PathVariable Long id, @RequestBody BrigadeDTO dto) {
        if (repository.existsById(id)) {
            var entity = mapper.mapToEntity(dto);
            entity.setId(dto.id());
            return mapper.mapToDto(repository.save(entity));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Brigade not found");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_BRIGADES')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
