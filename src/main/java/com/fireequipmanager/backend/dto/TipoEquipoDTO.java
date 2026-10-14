package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoEquipoDTO {

    // Se usa en la respuesta (al crear viaja como null)
    private Long id;

    @NotBlank(message = "El nombre del tipo de equipo es obligatorio")
    private String nombre;

    // Campo opcional para dar más detalles sobre la categoría (ej. EPP, Extintores, etc.)
    private String descripcion;
}