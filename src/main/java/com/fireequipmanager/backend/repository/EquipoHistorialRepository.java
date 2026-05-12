package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.EquipoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipoHistorialRepository extends JpaRepository<EquipoHistorial, Long> {
    List<EquipoHistorial> findByEquipoId(Long equipoId);
}