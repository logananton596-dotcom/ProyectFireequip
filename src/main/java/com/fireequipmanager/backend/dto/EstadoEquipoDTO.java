package com.fireequipmanager.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoEquipoDTO {

    // Se usa solo en la respuesta (al crear viaja como null)
    private Long id;

    // Validación para asegurar que el nombre del estado no llegue vacío
    @NotBlank(message = "El nombre del estado es obligatorio")
    private String nombre;
}