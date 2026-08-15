package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import com.fireequipmanager.backend.model.enumsEpp.EstadoEpp;
import com.fireequipmanager.backend.model.enumsEpp.NombreEpp;
import com.fireequipmanager.backend.model.enumsEpp.TipoEpp;
import com.fireequipmanager.backend.service.contracts.StockDTO;
import com.fireequipmanager.backend.service.contracts.VidaUtilDTO;
import com.fireequipmanager.backend.model.enumsArea.NombreArea;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EppDTO  implements VidaUtilDTO, StockDTO {

    // Id del EPP
    private Long id;

    // Código patrimonial CGBVP
    // CORRECCIÓN: Se eliminó el texto basura dentro del @NotBlank
    @NotBlank(message = "El código CGBVP es obligatorio")
    @Size(max = 30, message = "El código CGBVP no puede superar los 30 caracteres")
    private String codigoCgbvp;

    // Número de serie
    @NotBlank(message = "El número de serie es obligatorio")
    @Size(max = 50, message = "El número de serie no puede superar los 50 caracteres")
    private String numeroSerie;

    // Tipo de EPP
    @NotNull(message = "Debe seleccionar un tipo de EPP")
    private TipoEpp tipoEpp;

    // Nombre del EPP
    @NotNull(message = "Debe seleccionar un nombre de EPP")
    private NombreEpp nombreEpp;

    // Compañía propietaria
    @NotNull(message = "Debe seleccionar una compañía")
    private CompaniasBombero compania; // CORRECCIÓN: Tipo de dato unificado

    // Estado
    @NotNull(message = "Debe seleccionar un estado")
    private EstadoEpp estado;

    // Fecha de incorporación
    @NotNull(message = "La fecha de incorporación es obligatoria")
    private LocalDate fechaIncorporacion;

    // Vida útil
    @NotNull(message = "La vida útil es obligatoria")
    @Positive(message = "La vida útil debe ser mayor que cero")
    private Integer vidaUtilAnios;

    // Fecha de baja
    private LocalDate fechaBaja;

    // Motivo de baja
    @Size(max = 300, message = "El motivo de baja no puede superar los 300 caracteres")
    private String motivoBaja;

    // Talla
    @Size(max = 20, message = "La talla no puede superar los 20 caracteres")
    private String talla;

    // Material
    @Size(max = 80, message = "El material no puede superar los 80 caracteres")
    private String material;

    // Marca
    @Size(max = 80, message = "La marca no puede superar los 80 caracteres")
    private String marca;

    // Color
    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    private String color;

    // Observaciones
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String observaciones;

      // El frontend envía el Enum de texto aquí ('AREA_OPERACIONES')
    @NotNull(message = "Debe seleccionar un áreaA")
    private NombreArea nombreArea; 

    // Estado de asignación
    private Boolean asignado;

    //reglas
    private LocalDate fechaVencimiento;
    private Long diasRestantes;
    private Boolean proximoAVencer;
    private Boolean vidaUtilVencida;

    // ==========================================================
    // REGLAS: STOCK INDICADORES (🚀 AGREGA ESTAS TRES LÍNEAS)
    // ==========================================================
    private Boolean alertaStock;
    private Boolean sinStock;
    private Integer porcentajeStock; // Lombok generará el método setPorcentajeStock(Integer) aquí
}