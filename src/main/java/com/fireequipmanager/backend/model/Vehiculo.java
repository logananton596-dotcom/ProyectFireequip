package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fireequipmanager.backend.model.enumsVehiculo.*;

@Entity
@Table(
    name = "vehiculo",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo"),
        @UniqueConstraint(columnNames = "placa")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============ IDENTIFICACIÓN ============
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombreVehiculo;

    @Column(unique = true, length = 15)
    private String placa;

    @Column(nullable = false, length = 80)
    private String marca;

    @Column(length = 80)
    private String modelo;

    private Integer anio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    @Column(length = 30)
    private String numeroMotor;

    @Column(length = 30)
    private String numeroChasis;

    @Column(length = 500)
    private String observacion;

    @Column(nullable = false)
    private Boolean activo = true;

    // ============ CAPACIDADES ============
    private Integer capacidadAguaLitros;
    private Integer capacidadCombustibleGalones;

    // ============ COMBUSTIBLE Y ACEITE ============
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoCombustible tipoCombustible;

    @Column(length = 50)
    private String tipoAceite;

    private Integer intervaloCambioAceiteKm;

    // ============ LLANTAS ============
    @Column(length = 50)
    private String marcaLlanta;

    @Column(length = 50)
    private String medidaLlanta;

    @Column(length = 20)
    private String tamanoAro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoLlanta tipoLlanta;

    @Column(length = 20)
    private String presionLlanta;

    // ============ BATERÍA ============
    @Column(length = 50)
    private String tipoBateria;

    @Column(length = 50)
    private String marcaBateria;

    @Column(length = 30)
    private String amperajeBateria;

    private Integer numeroPlacasBateria;

    // ============ DOCUMENTACIÓN LEGAL ============
    private LocalDate soatVigencia;
    private LocalDate revisionTecnicaVigencia;

    // ============ SERVICIO ============
    private Integer tiempoServicioAnios;
    private LocalDate fechaIncorporacion;

    // ============ RELACIONES ============
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_nodo_id")
    private Ubicacion ubicacionNodo;

    // @OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
    // private List<Mantenimiento> mantenimientos;

    // ============ AUDITORÍA ============
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (estado == null) estado = EstadoVehiculo.OPERATIVO;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}