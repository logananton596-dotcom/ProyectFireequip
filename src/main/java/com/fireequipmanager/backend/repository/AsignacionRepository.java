package com.fireequipmanager.backend.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.Asignacion;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Obtener asignación activa de un equipo
    Optional<Asignacion> findByEquipoIdAndFechaFinIsNull(Long equipoId);

    // Historial completo
    List<Asignacion> findByEquipoId(Long equipoId);
}