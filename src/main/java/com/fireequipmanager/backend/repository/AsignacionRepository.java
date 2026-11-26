package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Asignacion;

import com.fireequipmanager.backend.model.enumsAsignacion.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // ==========================
    // CONSULTAS PRINCIPALES
    // ==========================

    List<Asignacion> findByBomberoId(Long bomberoId);

    List<Asignacion> findByEquipoId(Long equipoId);

    List<Asignacion> findByEppId(Long eppId);

    List<Asignacion> findByVehiculoId(Long vehiculoId);

    // Busca un bombero por su casillero asignado
    List<Asignacion> findByUbicacionId(Long ubicacionId);
    // Verifica si un casillero ya está asignado a un bombero
    boolean existsByUbicacionId(Long casilleroId);

    // Verifica si un casillero está asignado a otro bombero (al actualizar)
    boolean existsByUbicacionIdAndIdNot(Long casilleroId, Long id);


    List<Asignacion> findByEstado(
            EstadoAsignacion estado
    );

    List<Asignacion> findByActivoTrue();

    List<Asignacion> findByEstadoAndActivoTrue(
            EstadoAsignacion estado
    );

    // ==========================
    // VALIDACIONES
    // ==========================

    boolean existsByEquipoIdAndEstado(
            Long equipoId,
            EstadoAsignacion estado
    );

    boolean existsByEppIdAndEstado(
            Long eppId,
            EstadoAsignacion estado
    );

    boolean existsByBomberoIdAndEstado(
            Long bomberoId,
            EstadoAsignacion estado
    );

}