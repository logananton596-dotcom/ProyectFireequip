package com.fireequipmanager.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.TipoEquipo;

public interface TipoEquipoRepository extends JpaRepository<TipoEquipo, Long> {
    boolean existsByNombre(String nombre);
}
