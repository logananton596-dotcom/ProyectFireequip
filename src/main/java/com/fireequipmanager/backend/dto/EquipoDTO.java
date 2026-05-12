package com.fireequipmanager.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // <-- Esta anotación genera automáticamente los Getters y Setters
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private String nombre; // Asegúrate de que el nombre sea exactamente "nombre"
    private String numeroSerie;
    // ... otros campos
}
