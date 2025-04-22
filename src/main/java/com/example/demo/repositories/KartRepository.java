package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {
    Optional<Object> findById(String id);
}