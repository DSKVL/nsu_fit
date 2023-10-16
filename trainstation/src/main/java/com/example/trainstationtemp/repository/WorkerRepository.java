package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.Brigade;
import com.example.trainstationtemp.entity.domain.Department;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.service.TrainstationSpecification;
import org.springframework.data.domain.ManagedTypes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findAll(Specification<Worker> specification, Pageable of);

    Collection<Worker> findAllByBrigade(Brigade brigade, TrainstationSpecification<Worker> specification, PageRequest of);
}
