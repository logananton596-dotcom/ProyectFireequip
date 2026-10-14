package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsUbicacion.NombreOficina;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OficinaDTO {

    // Id
    private Long id;

    // Nombre de la oficina
    @NotNull(message = "Debe seleccionar una oficina")
    private NombreOficina nombreOficina;

    // Piso
    @NotNull(message = "Debe seleccionar el piso")
    private PisoUbicacion pisoUbicacion;

    // Descripción exacta
    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    private String descripcionUbicacion;

    // Observaciones
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String observacion;

    // Disponible
    private Boolean activa;
    // Referencia extra
    @Size(max = 30, message = "La referencia no puede superar los 30 caracteres")
    private String referencia;
}