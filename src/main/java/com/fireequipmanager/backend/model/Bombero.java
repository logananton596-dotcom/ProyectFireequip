package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.model.enumsBombero.GradoBombero;
import com.fireequipmanager.backend.model.enumsBombero.TipoSangre;
import com.fireequipmanager.backend.model.enumsBombero.TipoCargo;
import com.fireequipmanager.backend.model.enumsBombero.TipoVehiculoLicencia;
import com.fireequipmanager.backend.model.enumsBombero.CompañiasBombero;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "bombero",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo_cgbvp"),
                @UniqueConstraint(columnNames = "dni")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bombero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código institucional CGBVP
    @Column(name = "codigo_cgbvp", nullable = false, unique = true, length = 10)
    private String codigoCgbvp;

    // Compañía del bombero (temporalmente String)
    @Enumerated(EnumType.STRING)
    @Column(name = "compania", nullable = false)
    private CompañiasBombero compania;

    // Fecha de incorporación
    @Column(name = "fecha_incorporacion", nullable = false)
    private LocalDate fechaIncorporacion;

//DATOS PERSONALES
    // Nombres
    @Column(nullable = false, length = 100)
    private String nombre;

    // Apellidos
    @Column(nullable = false, length = 100)
    private String apellido;

    // Documento Nacional de Identidad
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    // Fecha de nacimiento
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

//DATOS FISICOS
    // Tipo de sangre
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sangre", nullable = false)
    private TipoSangre tipoSangre;

    // Talla en metros
    @Column(nullable = false)
    private Float talla;

    // Peso en kilogramos
    @Column(nullable = false)
    private Float peso;

//DATOS ADMINISTRATIVOS
    // Indica si actualmente ocupa un cargo
    @Column(nullable = false)
    private Boolean tieneCargo = false;

    // Cargo institucional
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cargo", nullable = false)
    private TipoCargo tipoCargo = TipoCargo.NINGUNO;

    // Fecha de inicio del cargo
    @Column(name = "fecha_inicio_cargo")
    private LocalDate fechaInicioCargo;

    // Fecha de finalización del cargo
    @Column(name = "fecha_fin_cargo")
    private LocalDate fechaFinCargo;

//DATOS INSTITUCIONALES
    // Grado institucional
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradoBombero grado;

//CONTACTO
    // Teléfono principal
    @Column(length = 9)
    private String telefono;

    // Teléfono de emergencia
    @Column(name = "telefono_emergencia", length = 9)
    private String telefonoEmergencia;

    // Correo electrónico
    @Column(length = 120)
    private String correo;

 //LICENCIA DE CONDUCIR
    // Indica si posee licencia de conducir
    @Column(nullable = false)
    private Boolean licencia = false;

    // Categoría de licencia (A-I, A-IIa, A-IIb, etc.)
    @Column(name = "tipo_licencia", length = 20)
    private String tipoLicencia;

    // Tipo de vehículo autorizado
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo_licencia", nullable = false)
    private TipoVehiculoLicencia tipoVehiculoLicencia = TipoVehiculoLicencia.NINGUNO;

    // Observaciones o restricciones médicas
    @Column(name = "limitacion_salud", length = 300)
    private String limitacionSalud;

//DATOS DE ESTADO
    // Estado administrativo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBombero estado = EstadoBombero.ACTIVO;

    @Column(name = "motivo_estado", length = 300)
    private String motivoEstado;

    // Historial de asignaciones
    @OneToMany(mappedBy = "bombero")
    private List<AsignacionEquipo> asignaciones;

}