package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsVehiculo.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {

    private Long id;

    @NotBlank(message = "El código del vehículo es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del vehículo es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombreVehiculo;

    @Size(max = 15, message = "La placa no puede superar los 15 caracteres")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 80, message = "La marca no puede superar los 80 caracteres")
    private String marca;

    @Size(max = 80, message = "El modelo no puede superar los 80 caracteres")
    private String modelo;

    private Integer anio;

    @NotNull(message = "Debe seleccionar un tipo")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "Debe seleccionar un estado")
    private EstadoVehiculo estado;

    @Size(max = 30)
    private String numeroMotor;

    @Size(max = 30)
    private String numeroChasis;

    @Size(max = 500)
    private String observacion;

    private Boolean activo;

    // Capacidades
    private Integer capacidadAguaLitros;
    private Integer capacidadCombustibleGalones;

    // Combustible y aceite
    private TipoCombustible tipoCombustible;

    @Size(max = 50)
    private String tipoAceite;

    private Integer intervaloCambioAceiteKm;

    // Llantas
    @Size(max = 50)
    private String marcaLlanta;

    @Size(max = 50)
    private String medidaLlanta;

    @Size(max = 20)
    private String tamanoAro;

    private TipoLlanta tipoLlanta;

    @Size(max = 20)
    private String presionLlanta;

    // Batería
    @Size(max = 50)
    private String tipoBateria;

    @Size(max = 50)
    private String marcaBateria;

    @Size(max = 30)
    private String amperajeBateria;

    private Integer numeroPlacasBateria;

    // Documentación legal
    private LocalDate soatVigencia;
    private LocalDate revisionTecnicaVigencia;

    // Servicio
    private Integer tiempoServicioAnios;
    private LocalDate fechaIncorporacion;

    // Ubicación
    @NotNull(message = "Debe seleccionar la ubicación del vehículo")
    private Long ubicacionNodoId;

    private String ubicacionNodoNombre;
    private String ubicacionNodoCodigo;
}