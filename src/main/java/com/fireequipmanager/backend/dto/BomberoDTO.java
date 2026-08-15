package com.fireequipmanager.backend.dto;

import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.model.enumsBombero.GradoBombero;
import com.fireequipmanager.backend.model.enumsBombero.TipoSangre;
import com.fireequipmanager.backend.model.enumsBombero.TipoCargo;
import com.fireequipmanager.backend.model.enumsBombero.TipoVehiculoLicencia;
import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BomberoDTO {

    // Identificador único
    private Long id;

    // Código institucional CGBVP
    @NotBlank(message = "El código CGBVP es obligatorio")
    @Pattern(
            regexp = "^[A-Za-z][0-9]{5}$",
            message = "El código CGBVP debe tener el formato A11111"
    )
    private String codigoCgbvp;

    // Compañía del bombero
    @NotNull(message = "La compañía es obligatoria")
    private CompaniasBombero compania;

    // Fecha de incorporación
    @NotNull(message = "La fecha de incorporación es obligatoria")
    private LocalDate fechaIncorporacion;

    // Nombres
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    // Apellidos
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    // Documento Nacional de Identidad
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^[0-9]{8}$",
            message = "El DNI debe contener exactamente 8 dígitos"
    )
    private String dni;

    // Fecha de nacimiento
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    // Tipo de sangre
    @NotNull(message = "El tipo de sangre es obligatorio")
    private TipoSangre tipoSangre;

    // Talla en metros
    @NotNull(message = "La talla es obligatoria")
    private Float talla;
    
    // Peso en kilogramos
    @NotNull(message = "El peso es obligatorio")
    private Float peso;

    // Indica si ocupa un cargo
    @NotNull(message = "Debe indicar si el bombero tiene cargo")
    private Boolean tieneCargo;

    // Cargo institucional
    @NotNull(message = "El tipo de cargo es obligatorio")
    private TipoCargo tipoCargo;

    // Fecha de inicio del cargo
    private LocalDate fechaInicioCargo;

    // Fecha de fin del cargo
    private LocalDate fechaFinCargo; 

    // Grado institucional
    @NotNull(message = "El grado es obligatorio")
    private GradoBombero grado;

    // Teléfono principal
    @Pattern(
            regexp = "^$|^[0-9]{9}$",
            message = "El teléfono debe contener exactamente 9 dígitos"
    )
    private String telefono;

    // Teléfono de emergencia
    @Pattern(
            regexp = "^$|^[0-9]{9}$",
            message = "El teléfono de emergencia debe contener exactamente 9 dígitos"
    )
    private String telefonoEmergencia;

    // Correo electrónico
    @Pattern(
        regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "El correo electrónico no tiene un formato válido"
    )
    private String correo;

    // ¿Posee licencia?
    @NotNull(message = "Debe indicar si posee licencia")
    private Boolean licencia;

    // Categoría de licencia
    @Size(max = 20, message = "El tipo de licencia no puede superar los 20 caracteres")
    private String tipoLicencia;

    // Tipo de vehículo autorizado
    @NotNull(message = "El tipo de vehículo autorizado es obligatorio")
    private TipoVehiculoLicencia tipoVehiculoLicencia;

    // Restricciones o limitaciones médicas
    @Size(max = 300, message = "La limitación de salud no puede superar los 300 caracteres")
    private String limitacionSalud;

    // Estado administrativo
    @NotNull(message = "El estado es obligatorio")
    private EstadoBombero estado;

    @Size(max = 300)
    private String motivoEstado;

}