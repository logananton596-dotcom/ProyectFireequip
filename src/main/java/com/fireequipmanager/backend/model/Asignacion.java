package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsAsignacion.EstadoAsignacion;
import com.fireequipmanager.backend.model.enumsAsignacion.TipoDestinoAsignacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Equipo asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    // EPP asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epp_id")
    private Epp epp;

    // Tipo de destino
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private TipoDestinoAsignacion tipoDestino;

    // Bombero destinatario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bombero_id")
    private Bombero bombero;

    // Vehículo destinatario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    // Ubicación física destinataria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    // Fecha en que se realizó la asignación
    @Column(nullable = false)
    private LocalDate fechaAsignacion;

    // Fecha de devolución
    private LocalDate fechaDevolucion;

    // Fecha límite de la asignación
    private LocalDate fechaFin;

    // Estado de la asignación
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private EstadoAsignacion estado = EstadoAsignacion.ACTIVA;

    // Observaciones
    @Column(length = 500)
    private String observaciones;

    // Indica si la asignación está activa
    @Column(nullable = false)
    private Boolean activo = true;

    // Auditoría
    @Column(
            updatable = false,
            nullable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartimiento_id")
    private CompartimientoVehiculo compartimiento;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoAsignacion.ACTIVA;
        }

        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();
    }
}