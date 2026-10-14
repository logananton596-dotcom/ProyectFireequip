package com.fireequipmanager.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.Mantenimiento;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    // Obtener mantenimientos por equipo
    List<Mantenimiento> findByEquipoId(Long equipoId);
}