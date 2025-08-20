package com.example.Khebra.repository;

import com.example.Khebra.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCin(String cin);

    User findUserByEmail(String email);
}
