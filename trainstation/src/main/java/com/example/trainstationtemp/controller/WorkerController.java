package com.example.trainstationtemp.controller;

import com.example.trainstationtemp.dto.WorkerDTO;
import com.example.trainstationtemp.repository.WorkerRepository;
import com.example.trainstationtemp.entity.domain.Worker;
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
@RequestMapping("/workers")
public class WorkerController {
    @Autowired private WorkerRepository workerRepository;
    @Autowired private DtoMappings dtoMappings;

    public WorkerController() {
    }

    @PreAuthorize("hasAuthority('READ_WORKER')")
    @GetMapping
    public List<WorkerDTO> get(@RequestParam(name = "page") Integer pageId,
                               @RequestParam(name = "sort") Optional<String> sort,
                               @RequestParam(name = "filter") Optional<String> filter) {
        var specification = new TrainstationSpecification<Worker>(filter);
        var sortReq = sort.map(s -> switch (s) {
            case "salary", "kids", "brigade" -> Sort.by(Sort.Direction.ASC, s);
            default -> Sort.unsorted();
        }).orElseGet(Sort::unsorted);

        return workerRepository
                .findAll(specification, PageRequest.of(pageId, 10, sortReq))
                .stream()
                .map(dtoMappings::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_WORKER')")
    public WorkerDTO get(@PathVariable Long id) {
        return dtoMappings.mapToDto(
                workerRepository
                        .findById(id)
                        .orElseThrow(RuntimeException::new)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_WORKER')")
    public WorkerDTO post(@RequestBody WorkerDTO worker) {
        try {
            Worker recievedWorker = dtoMappings.mapToEntity(worker);
            Worker savedWorker = workerRepository.save(recievedWorker);
            return dtoMappings.mapToDto(savedWorker);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Worker not Found", e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_WORKER')")
    public WorkerDTO put(@PathVariable Long id, @RequestBody WorkerDTO worker) {
        try {
            Worker currentWorker = workerRepository.findById(id).orElseThrow();
            Worker recievedWorker = dtoMappings.mapToEntity(worker);
            currentWorker.assign(recievedWorker);
            Worker savedWorker = workerRepository.save(recievedWorker);
            return dtoMappings.mapToDto(savedWorker);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Brigade or worker not Found", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_WORKER')")
    public Boolean delete(@PathVariable Long id) {
        var inRepo = workerRepository.existsById(id);
        if (inRepo) workerRepository.deleteById(id);
        return inRepo;
    }
}
