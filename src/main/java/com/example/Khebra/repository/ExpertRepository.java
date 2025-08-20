package com.example.Khebra.repository;

import com.example.Khebra.entity.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Integer>, JpaSpecificationExecutor<Expert> {
    Optional<Expert> findByEmail(String email);
    Page<Expert> findAll(Specification<Expert> spec, Pageable pageable);

//    @Query("SELECT e FROM Expert e JOIN e.domaine d WHERE d.name = :domaineNom AND e.isValidated = true")
//    Page<Expert> findByDomaine(@Param("domaineNom") String domaineNom, Pageable pageable);

}