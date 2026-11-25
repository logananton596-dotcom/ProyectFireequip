package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.MarcaVehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.NombreVehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.TipoVehiculo;
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
public class VehiculoDTO {

    private Long id;

    // Código interno requerido por la Entidad
    @NotBlank(message = "El código del vehículo es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigo;

    @NotNull(message = "Debe seleccionar un vehículo")
    private NombreVehiculo nombreVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 15, message = "La placa no puede superar los 15 caracteres")
    private String placa;

    @NotNull(message = "Debe seleccionar una marca")
    private MarcaVehiculo marca;

    @NotNull(message = "Debe seleccionar un tipo")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "Debe seleccionar un estado")
    private EstadoVehiculo estado;

    @Size(max = 80, message = "El modelo no puede superar los 80 caracteres")
    private String modelo;

    private Integer anio;
    private Integer capacidadAgua;
    private Integer capacidadEspuma;

    @Size(max = 500, message = "La observación no puede superar los 500 caracteres")
    private String observacion; // Unificado con la entidad

    private Boolean activo; // Unificado con la entidad

    // =========================================================
    // UBICACIÓN
    // =========================================================

    // ID de la ubicación física (opcional; agrega @NotNull si es obligatoria)
    @Positive(message = "El ID de ubicación debe ser válido")
    private Long ubicacionId;

    // Campos de solo lectura para mostrar en listados
    private String ubicacionCodigo;
    private String ubicacionNombre;
    private String ubicacionTipo;
    private String ubicacionDisplay;

    // Campos de mantenimiento mecánico agregados en la Entidad
    @Size(max = 30, message = "El número de motor no puede superar los 30 caracteres")
    private String numeroMotor;

    @Size(max = 30, message = "El número de chasis no puede superar los 30 caracteres")
    private String numeroChasis;
}
