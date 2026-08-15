package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsArea.NombreArea;
import com.fireequipmanager.backend.model.enumsBien.EstadoBien;
import com.fireequipmanager.backend.model.enumsBien.TipoBien;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BienDTO {

    // Identificador del bien
    private Long id;

    // Código interno o patrimonial
    @NotBlank(message = "El código de inventario es obligatorio")
    @Size(
            max = 30,
            message = "El código de inventario no puede superar los 30 caracteres"
    )
    private String codigoCgbvp;

    // Nombre del bien
    @NotBlank(message = "El nombre del bien es obligatorio")
    @Size(
            max = 100,
            message = "El nombre del bien no puede superar los 100 caracteres"
    )
    private String nombre;

    // Tipo de bien
    @NotNull(message = "Debe seleccionar un tipo de bien")
    private TipoBien tipoBien;

    // Marca
    @Size(
            max = 80,
            message = "La marca no puede superar los 80 caracteres"
    )
    private String marca;

    // Modelo
    @Size(
            max = 80,
            message = "El modelo no puede superar los 80 caracteres"
    )
    private String modelo;

    // Número de serie
    @Size(
            max = 80,
            message = "El número de serie no puede superar los 80 caracteres"
    )
    private String numeroSerie;

    // Fecha de adquisición
    @NotNull(message = "La fecha de adquisición es obligatoria")
    private LocalDate fechaAdquisicion;

    // Estado actual
    @NotNull(message = "Debe seleccionar el estado del bien")
    private EstadoBien estado;

    // Condición física
    @Size(
            max = 50,
            message = "La condición no puede superar los 50 caracteres"
    )
    private String condicion;

    // Valor referencial
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El valor referencial no puede ser negativo"
    )
    private BigDecimal valorReferencial;

    // Especificaciones particulares
    @Size(
            max = 1000,
            message = "Las especificaciones no pueden superar los 1000 caracteres"
    )
    private String especificaciones;

    // Observaciones
    @Size(
            max = 500,
            message = "Las observaciones no pueden superar los 500 caracteres"
    )
    private String observaciones;

    // Área propietaria
    @NotNull(message = "Debe seleccionar el área propietaria")
    private NombreArea area;

    // =========================================================
    // UBICACIÓN
    // =========================================================

    // ID de la ubicación física
    @NotNull(message = "Debe seleccionar la ubicación del bien")
    @Positive(message = "El ID de ubicación debe ser válido")
    private Long ubicacionId;

    // Información descriptiva de la ubicación
    private String codigoUbicacion;

    private NombreUbicacion nombreUbicacion;

    // Activo lógico
    private Boolean activo;
}