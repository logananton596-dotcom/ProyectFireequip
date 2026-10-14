package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsUbicacion.MaterialCasillero;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "casillero",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo"),
                @UniqueConstraint(columnNames = "numero")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Casillero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, unique = true)
    private Integer numIdentificadorCasillero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialCasillero materialCasillero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PisoUbicacion pisoUbicacion;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean ocupado = false;

    @Column(nullable = false)
    private Boolean asignado = false; // 🚀 Reubicado aquí de manera limpia

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false, unique = true)
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
        if (ocupado == null) {
            ocupado = false;
        }
        if (asignado == null) { // 🚀 Asegura la inicialización en la base de datos
            asignado = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
