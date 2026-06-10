package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {

    // Se usa solo en la respuesta (al crear viaja como null)
    private Long id;

    @NotBlank(message = "El código interno es obligatorio")
    private String codigoInterno;

    @NotBlank(message = "El número de serie es obligatorio")
    private String numeroSerie;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El área es obligatoria")
    private String area;

    private String marca;
    private String modelo;
    private LocalDate fechaCompra;

    @Positive(message = "La vida útil debe ser un número positivo")
    private Integer vidaUtilAnios;

    private String ubicacionActual;

    // El cliente envía solo estos IDs al crear/editar
    @NotNull(message = "El ID del tipo de equipo es obligatorio")
    private Long tipoEquipoId;
    // El servidor llena estos nombres solo para las respuestas (GET/POST/PUT)
    private String tipoEquipoNombre;
    private String estadoEquipoNombre;

    // Datos de baja (pueden ser null si el equipo está activo)
    private String motivoBaja;
    private String autorizadoPor;
    private LocalDate fechaBaja;

    // Datos de auditoría para la respuesta
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ASIGNAR POR AREA
    @NotNull(message = "El ID del área es obligatorio")
    private Long areaId;
    private String areaNombre;
}