package com.fireequipmanager.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionEquipoDTO {

    private Long id;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Long equipoId;

    @NotNull(message = "El ID del bombero es obligatorio")
    private Long bomberoId; 

    // Datos informativos del Equipo para las respuestas (GET)
    private String equipoCodigoInterno;
    private String equipoNumeroSerie;
    private String equipoNombre;
    private String tipoEquipoNombre;
    private String equipoMarca; 
    // Datos informativos del Bombero para las respuestas (GET)
    private String bomberoCodigo;
    private String bomberoNombre;
    private String bomberoGrado;

    // Especificaciones de la entrega
    private String tallaEquipo;
    private String caracteristicasEspecificas;
    private LocalDate fechaPuestaOperatividad;
    private LocalDate fechaCaducidad;
 
      // --- CAMBIO: Validaciones estándar para String ---
    @NotBlank(message = "El estado físico de entrega es obligatorio")
    private String estadoFisicoEntrega; 

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento; 

    // Responsable de la entrega
    @NotBlank(message = "El nombre del responsable es obligatorio")
    private String responsableNombre;
    @NotBlank(message = "El código del responsable es obligatorio")
    private String responsableCodigo;
    private String responsableGrado;
    private String responsableCargo;
    private String responsableTelefono;

     // Auditoría
     private LocalDateTime fechaHoraCreacion;
     private LocalDateTime fechaHoraActualizacion;
     private LocalDateTime fechaHoraEliminacion;
     private LocalDateTime fechaHoraRestauracion;
     private LocalDateTime fechaHoraUltimoMovimiento; // NUEVO: Fecha de la última entrega o devolución
     private LocalDateTime fechaHoraDevolucion; // NUEVO: Fecha de devolución del equipo
     private LocalDateTime fechaHoraEntrega; // NUEVO: Fecha de entrega del equipo
}