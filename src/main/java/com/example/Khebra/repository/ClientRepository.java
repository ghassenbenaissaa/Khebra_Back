package com.example.Khebra.repository;

import com.example.Khebra.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByEmail(String email);
    Integer findIdByEmail(String email);
    Client findClientById(Integer id);
    Page<Client> findAll(Specification<Client> spec, Pageable pageable);
    Client findClientByEmail(String email);

}
