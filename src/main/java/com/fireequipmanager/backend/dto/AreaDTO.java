package com.fireequipmanager.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {

    private Long id;

    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El encargado del área es obligatorio")
    @Size(max = 150, message = "El nombre del encargado no puede superar los 150 caracteres")
    private String encargado;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;
}