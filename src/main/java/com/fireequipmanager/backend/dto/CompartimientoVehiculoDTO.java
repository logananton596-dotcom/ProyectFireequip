package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsUbicacion.TipoCompartimiento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompartimientoVehiculoDTO {

    // Id
    private Long id;

    // Vehículo al que pertenece
    @NotNull(message = "Debe seleccionar un vehículo")
    private Long vehiculoId;

    // Nombre del vehículo (respuesta)
    private String vehiculoNombre;

    // Nombre del compartimiento
    @NotNull(message = "El tipo de compartimiento es obligatorio")
    private TipoCompartimiento tipoCompartimiento; 
    // Código interno
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigo;

    // Descripción
    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String descripcion;

    // Orden dentro del vehículo
    private Integer orden;

    // Disponible
    private Boolean activo;
}