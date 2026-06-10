package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomberoDTO {

    private Long id;

    @NotBlank(message = "El código del bombero es obligatorio")
    @Size(max = 30)
    private String codigo;

    @NotBlank(message = "El nombre del bombero es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank(message = "El grado del bombero es obligatorio")
    @Size(max = 50)
    private String grado;

    @Size(max = 20)
    private String telefono;

    private boolean activo;
}