package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.Brigade;
import com.example.trainstationtemp.entity.domain.Driver;
import com.example.trainstationtemp.entity.domain.Worker;
import com.example.trainstationtemp.service.TrainstationSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DriverRepository  extends JpaRepository<Driver, Long> {
    Collection<Driver> findAll(Specification<Driver> specification, Pageable of);
}
