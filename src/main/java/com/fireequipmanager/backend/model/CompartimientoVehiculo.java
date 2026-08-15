package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsUbicacion.TipoCompartimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "compartimiento_vehiculo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompartimientoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código interno
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Nombre visible
    @Column(nullable = false, length = 80)
    private String nombre;

    // Tipo de compartimiento
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCompartimiento tipoCompartimiento;

    // Descripción
    @Column(length = 300)
    private String descripcion;

    // Activo
    @Column(nullable = false)
    private Boolean activo = true;

    // Vehículo al que pertenece
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    // Ubicación asociada
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false, unique = true)
    private Ubicacion ubicacion;

    // Auditoría
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (activo == null) {
            activo = true;
        }

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //orden
    // Orden dentro del vehículo
    @Column(nullable = false)
    private Integer orden;
}