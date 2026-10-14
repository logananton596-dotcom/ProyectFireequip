package com.fireequipmanager.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MantenimientoDTO {

    // Se autogenera en el servidor (al crear viaja como null)
    private Long id;

    // El cliente envía solo el ID del equipo al registrar el mantenimiento
    @NotNull(message = "El ID del equipo es obligatorio")
    private Long equipoId;

    // El servidor llena estos campos del equipo solo para las respuestas (GET/POST)
    private String equipoCodigoInterno;
    private String equipoNombre;
    private String equipoMarca;

    // Fecha en la que se realizó o se iniciará el mantenimiento
    @NotNull(message = "La fecha de mantenimiento es obligatoria")
    private LocalDate fecha;

    // Tipo: PREVENTIVO o CORRECTIVO
    @NotBlank(message = "El tipo de mantenimiento (PREVENTIVO/CORRECTIVO) es obligatorio")
    private String tipo;

    // Descripción detallada del trabajo realizado
    @NotBlank(message = "La descripción del mantenimiento es obligatoria")
    private String descripcion;

    // Persona o empresa externa responsable de la reparación/revisión
    @NotBlank(message = "El responsable del mantenimiento es obligatorio")
    private String responsable;

    // Fecha estimada para la siguiente revisión (útil para alertas de mantenimiento preventivo)
    private LocalDate fechaProximo;
    
    // Fecha en la que culminó el mantenimiento (puede ser null si está en proceso)
    private LocalDate fechaFin;
}