package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {

    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede superar los 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;

    @NotNull(message = "Debe seleccionar el tipo de ubicación")
    private TipoUbicacion tipo;

    @Size(max = 50, message = "El tipo personalizado no puede superar los 50 caracteres")
    private String tipoPersonalizado;

    // Padre (jerarquía)
    private Long padreId;
    private String padreNombre;

    // Área propietaria (opcional)
    private Long areaId;
    private String areaNombre;

    private Boolean activa;

    @Size(max = 300, message = "La observación no puede superar los 300 caracteres")
    private String observacion;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Hijos directos (solo lectura, para vistas de árbol)
    private List<UbicacionDTO> hijos;

    // Solo lectura: bombero asignado a un casillero
    private Long bomberoAsignadoId; 
    private String bomberoAsignadoNombre;

}