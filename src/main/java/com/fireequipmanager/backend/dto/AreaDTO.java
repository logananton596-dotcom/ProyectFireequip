package com.fireequipmanager.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fireequipmanager.backend.model.enumsArea.NombreArea;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {

    // Id del área
    private Long id;

    @NotNull(message = "Debe seleccionar un área")
    private NombreArea nombreArea;

    // Encargado principal (Id del bombero)
    @NotNull(message = "Debe seleccionar un encargado principal")
    private Long encargado1Id;

    // Encargado secundario (Opcional)
    private Long encargado2Id;

    // Inicio del cargo
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    // Fin del cargo
    private LocalDate fechaFin;

    // Observaciones
    @Size(max = 300, message = "Las observaciones no pueden superar los 300 caracteres")
    private String observaciones;

    // Estado del área
    private Boolean activo;
}