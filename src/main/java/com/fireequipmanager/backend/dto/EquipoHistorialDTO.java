package com.fireequipmanager.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoHistorialDTO {

    private Long id;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDateTime fechaCambio;
    private String usuario; // Nombre del usuario que hizo el cambio

    // Relación simplificada con el equipo
    private Long equipoId;
    private String equipoCodigoInterno;
    private String equipoNombre;
}