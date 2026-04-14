package com.fireequipmanager.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.EstadoEquipo;

public interface EstadoEquipoRepository extends JpaRepository<EstadoEquipo, Long> {
    boolean existsByNombre(String nombre);
}
