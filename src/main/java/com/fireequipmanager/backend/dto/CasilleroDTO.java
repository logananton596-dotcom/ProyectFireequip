package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsUbicacion.MaterialCasillero;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasilleroDTO {

    private Long id;

    @NotBlank(message = "El código del casillero es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigo;

    // 🚀 CORREGIDO: CamelCase estándar
    @NotNull(message = "Debe registrar el número del casillero")
    @Positive(message = "El número debe ser mayor a cero")
    private Integer numIdentificadorCasillero;

    // 🚀 CORREGIDO: CamelCase estándar
    @NotNull(message = "Debe seleccionar el material")
    private MaterialCasillero materialCasillero;

    // 🚀 CORREGIDO: CamelCase estándar
    @NotNull(message = "Debe seleccionar el piso")
    private PisoUbicacion pisoUbicacion;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    private String descripcion; 

    private Boolean activo;
    private Boolean ocupado;
    private Boolean asignado;

    // 🚀 CORREGIDO: Cambiado a texto String para mapear el name() o toString() del Enum de Ubicación
    @NotBlank(message = "La ubicación asociada al casillero es obligatoria")
    private String ubicacionNombre; 
}
