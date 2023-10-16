package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.domain.MedicalExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalExaminationRepository extends JpaRepository<MedicalExamination, Long> {
}
