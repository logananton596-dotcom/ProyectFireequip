package com.fireequipmanager.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsoEmergenciaDTO {

    private Long id;

    @NotBlank(message = "El nombre del uso de emergencia es obligatorio")
    private String nombre;

    // Al recibir datos, el cliente envía solo la lista de IDs de los tipos permitidos
    @NotEmpty(message = "Debe asignar al menos un tipo de equipo permitido")
    private List<Long> tiposPermitidosIds;

    // Al responder, el servidor llena esta lista de objetos simplificados (id y nombre)
    private List<TipoEquipoResumenDTO> tiposPermitidos;

    // Subclase estática interna para no saturar con archivos adicionales
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoEquipoResumenDTO {
        private Long id;
        private String nombre;
    }
}