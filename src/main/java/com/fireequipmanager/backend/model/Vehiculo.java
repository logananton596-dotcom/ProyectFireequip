package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    // Código interno
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    // Nombre visible
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private NombreVehiculo nombreVehiculo;

    // Placa
    @Column(nullable = false, unique = true, length = 15)
    private String placa;

    // Marca
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarcaVehiculo marca;

    // Tipo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVehiculo tipoVehiculo;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    // Modelo
    @Column(length = 80)
    private String modelo;

    // Año
    private Integer anio;

    // Capacidad de agua
    private Integer capacidadAgua;

    // Capacidad de espuma
    private Integer capacidadEspuma;

    // Observaciones
    @Column(length = 500)
    private String observacion;

    // Activo
    @Column(nullable = false)
    private Boolean activo = true;

    // Ubicación física del vehículo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (activo == null) {
            activo = true;
        }

        if (estado == null) {
            estado = EstadoVehiculo.OPERATIVO;
        }

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
//atrib extras para mantenimiento de vehiculos
    @Column(length = 30)
    private String numeroMotor;

    @Column(length = 30)
    private String numeroChasis;

}