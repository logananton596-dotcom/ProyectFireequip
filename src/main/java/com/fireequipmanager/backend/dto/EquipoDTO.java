package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsArea.NombreArea;
import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import com.fireequipmanager.backend.model.enumsEquipo.EstadoEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.NombreEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.TipoEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.TipoInventario;
import com.fireequipmanager.backend.service.contracts.StockDTO;
import com.fireequipmanager.backend.service.contracts.VidaUtilDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO  implements VidaUtilDTO, StockDTO {

    // Id
    private Long id;

    // Código institucional (solo inventario individual)
    @Size(max = 30, message = "El código CGBVP no puede superar los 30 caracteres")
    private String codigoCgbvp;

    // Número de serie (solo inventario individual)
    @Size(max = 50, message = "El número de serie no puede superar los 50 caracteres")
    private String numeroSerie;

    // Nombre del equipo
    @NotNull(message = "Debe seleccionar un equipo")
    private NombreEquipo nombreEquipo;

    // Tipo
    @NotNull(message = "Debe seleccionar un tipo")
    private TipoEquipo tipoEquipo;

    // Individual o múltiple
    @NotNull(message = "Debe seleccionar el tipo de inventario")
    private TipoInventario tipoInventario;

    // Compañía propietaria
    @NotNull(message = "Debe seleccionar una compañía")
    private CompaniasBombero compania;

    // Estado actual
    @NotNull(message = "Debe seleccionar un estado")
    private EstadoEquipo estado;

    // Fecha de incorporación
    @NotNull(message = "La fecha de incorporación es obligatoria")
    private LocalDate fechaIncorporacion;

    // Vida útil
    @NotNull(message = "Debe registrar la vida útil")
    @Positive(message = "La vida útil debe ser mayor a cero")
    private Integer vidaUtilAnios;

    // Datos de baja
    private LocalDate fechaBaja;

    @Size(max = 300, message = "El motivo de baja no puede superar los 300 caracteres")
    private String motivoBaja;

    // Stock actual
    //@NotNull(message = "Debe registrar el stock")
    @Positive(message = "El stock debe ser mayor a cero")
    private Integer stock;

    // Stock mínimo
    @NotNull(message = "Debe registrar el stock mínimo")
    @Positive(message = "El stock mínimo debe ser mayor a cero")
    private Integer stockMinimo;

    // Especificación técnica
    @Size(max = 250, message = "La especificación no puede superar los 250 caracteres")
    private String especificacion;

    // Descripción administrativa
    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    private String descripcion;

    // Material
    @Size(max = 80, message = "El material no puede superar los 80 caracteres")
    private String material;

    // Marca
    @Size(max = 80, message = "La marca no puede superar los 80 caracteres")
    private String marca;

    // Modelo
    @Size(max = 80, message = "El modelo no puede superar los 80 caracteres")
    private String modelo;

    // Color
    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    private String color;

    // Observaciones
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String observaciones;

    // Área propietaria
    @NotNull(message = "Debe seleccionar un área")
    private NombreArea nombreArea; 

    // Estado de asignación
    private Boolean asignado;

    //atributos de reglas
    private Boolean alertaStock;
    private Boolean sinStock;
    private Integer porcentajeStock;
    private LocalDate fechaVencimiento;
    private Long diasRestantes;
    private Boolean proximoAVencer;
    private Boolean vidaUtilVencida;
    
}