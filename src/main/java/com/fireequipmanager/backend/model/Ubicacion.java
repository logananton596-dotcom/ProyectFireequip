package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsUbicacion.EstadoUbicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ubicacion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nombre")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único de la ubicación agregado para asignaciones mov pres y manteni
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Nombre visible de la ubicación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 120)
    private NombreUbicacion nombreUbicacion;

    // Estado de la ubicación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUbicacion estado = EstadoUbicacion.ACTIVA;

    // Observaciones
    @Column(length = 300)
    private String observacion;

    // Indica si la ubicación puede utilizarse
    @Column(nullable = false)
    private Boolean activa = true;

    // Auditoría
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @OneToOne(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Casillero casillero;

    // 2. Relación inversa con Oficina
    @OneToOne(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Oficina oficina;
    
    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoUbicacion.ACTIVA;
        }

        if (activa == null) {
            activa = true;
        }

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}