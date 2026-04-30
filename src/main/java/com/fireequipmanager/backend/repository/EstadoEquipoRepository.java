package com.fireequipmanager.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.EstadoEquipo;

public interface EstadoEquipoRepository extends JpaRepository<EstadoEquipo, Long> {

    Optional<EstadoEquipo> findByNombre(String nombre);

}
