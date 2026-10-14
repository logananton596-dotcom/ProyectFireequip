package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {

    // Se usa en la respuesta (al crear viaja como null)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre; // Ejemplo: ADMIN, OPERADOR
}