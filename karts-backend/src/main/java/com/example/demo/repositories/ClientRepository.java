package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<Object> findByRut(String rut);

    List<ClientEntity> findByRutIn(List<String> clientRuts);

    void deleteByRut(String rut);

}