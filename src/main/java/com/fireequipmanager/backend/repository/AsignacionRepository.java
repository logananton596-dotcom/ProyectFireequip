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

    List<Asignacion> findByUbicacionId(Long ubicacionId);

    List<Asignacion> findByCompartimientoId(Long compartimientoId);

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