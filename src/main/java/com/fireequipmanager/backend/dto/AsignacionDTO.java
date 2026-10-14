package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsAsignacion.EstadoAsignacion;
import com.fireequipmanager.backend.model.enumsAsignacion.TipoDestinoAsignacion;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDTO {

    private Long id;

    // =========================================================
    // RECURSO
    // =========================================================

    // ID del equipo
    private Long equipoId;

    // Información del equipo para respuestas
    private EquipoDTO equipo;

    // ID del EPP
    private Long eppId;

    // Información del EPP para respuestas
    private EppDTO epp;

    // =========================================================
    // DESTINO
    // =========================================================

    @NotNull(message = "Debe seleccionar el tipo de destino")
    private TipoDestinoAsignacion tipoDestino;

    // Bombero
    private Long bomberoId;

    private BomberoDTO bombero;

    // Vehículo
    private Long vehiculoId;

    private VehiculoDTO vehiculo;

    // Ubicación física
    private Long ubicacionId;

    private String codigoUbicacion;

    private NombreUbicacion nombreUbicacion;

    private UbicacionDTO ubicacion;

    // =========================================================
    // FECHAS
    // =========================================================

    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fechaAsignacion;

    private LocalDate fechaDevolucion;

    private LocalDate fechaFin;

    // =========================================================
    // ESTADO
    // =========================================================

    @NotNull(message = "Debe seleccionar el estado de la asignación")
    private EstadoAsignacion estado;

    // =========================================================
    // INFORMACIÓN ADICIONAL
    // =========================================================

    @Size(
            max = 500,
            message = "Las observaciones no pueden superar los 500 caracteres"
    )
    private String observaciones;

    private Boolean activo;

    private Long compartimientoId;

    private CompartimientoVehiculoDTO compartimiento;
    }