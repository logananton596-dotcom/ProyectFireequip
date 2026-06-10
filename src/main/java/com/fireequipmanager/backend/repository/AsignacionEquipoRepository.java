package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.AsignacionEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsignacionEquipoRepository extends JpaRepository<AsignacionEquipo, Long> {

    // 1. Historial del Bombero: Encuentra todas las asignaciones de un efectivo específico
    List<AsignacionEquipo> findByBomberoId(Long bomberoId);

    // 2. Historial del Equipo: Encuentra a qué bomberos se le ha asignado este equipo a lo largo del tiempo
    List<AsignacionEquipo> findByEquipoId(Long equipoId);

    // 3. Control Operativo: Filtra asignaciones según el estado físico en el que se entregó (ej: "Usado", "Nuevo")
    List<AsignacionEquipo> findByEstadoFisicoEntregaIgnoreCase(String estadoFisicoEntrega);

    // 4. Trazabilidad: Filtra por el tipo de movimiento (ej: "Alta por ingreso", "Reemplazo por desgaste")
    List<AsignacionEquipo> findByTipoMovimientoIgnoreCase(String tipoMovimiento);

    // 5. Alerta de Seguridad: Encuentra asignaciones cuyos equipos caduquen antes de una fecha determinada
    List<AsignacionEquipo> findByFechaCaducidadBefore(LocalDate fecha);
}