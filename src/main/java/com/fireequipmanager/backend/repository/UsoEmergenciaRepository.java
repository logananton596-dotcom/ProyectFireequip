package com.fireequipmanager.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fireequipmanager.backend.model.UsoEmergencia;

public interface UsoEmergenciaRepository extends JpaRepository<UsoEmergencia, Long> {
    Optional<UsoEmergencia> findByNombre(String nombre);
}