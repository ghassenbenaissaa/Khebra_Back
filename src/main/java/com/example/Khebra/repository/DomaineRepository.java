package com.example.Khebra.repository;

import com.example.Khebra.entity.Domaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DomaineRepository extends JpaRepository<Domaine, Integer> {
    @Query("SELECT DISTINCT d FROM Domaine d JOIN d.experts e WHERE SIZE(d.experts) > 0")
    List<Domaine> findDomainesWithExperts();
}
