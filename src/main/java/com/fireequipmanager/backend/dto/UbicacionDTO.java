package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsUbicacion.EstadoUbicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion; // Unificado al mismo Enum
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {

    private Long id;

    // 🚀 AGREGA EL CÓDIGO: Obligatorio en la Entidad
    @NotBlank(message = "El código de la ubicación es obligatorio")
    @Size(max = 30, message = "El código no puede superar los 30 caracteres")
    private String codigo;

    // Tipo / Nombre de ubicación (Unificado)
    @NotNull(message = "Debe seleccionar el tipo de ubicación")
    private NombreUbicacion nombreUbicacion;

    @NotNull(message = "Debe seleccionar el estado de la ubicación")
    private EstadoUbicacion estado;

    @Size(max = 300, message = "La observación no puede superar los 300 caracteres")
    private String observacion;

    // Relaciones Opcionales para las respuestas visuales
    private Long oficinaId;
    private String oficinaNombre;

    private Long casilleroId;
    private Integer numeroCasillero;

    private Long vehiculoId;
    private String vehiculoNombre;

    private Long compartimientoId;
    private String compartimientoNombre;

    private Boolean activa;
}
