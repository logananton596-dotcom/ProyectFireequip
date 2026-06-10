package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDTO {

    // Se genera en la respuesta (al crear viaja como null)
    private Long id;
    private Long usoEmergenciaId; 
    // El cliente envía solo el ID del equipo al crear/editar
    @NotNull(message = "El ID del equipo es obligatorio")
    private Long equipoId;

    // El servidor llena estos campos del equipo solo para las respuestas (GET/POST)
    private String equipoCodigoInterno;
    private String equipoNombre;

    // Tipo de asignación: ESTACION, VEHICULO, BOMBERO
    @NotBlank(message = "El tipo de asignación es obligatorio")
    private String tipoAsignacion;

    // Identificador del destino (nombre de la estación, unidad móvil o bombero)
    @NotBlank(message = "El destino de la asignación es obligatorio")
    private String destino;

    // Fecha de inicio (puedes validarla o dejar que el service use LocalDateTime.now() si viene null)
    private LocalDateTime fechaInicio;

    // Fecha fin (permanece null si la asignación está activa)
    private LocalDateTime fechaFin;
}