package com.example.Khebra.repository;

import com.example.Khebra.entity.Avis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Integer> {
    List<Avis> findByExpertIdAndEnabledTrue(int expertId);

}
