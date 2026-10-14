package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsUbicacion.NombreOficina;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "oficina",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código interno
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    // Nombre de la oficina
@Enumerated(EnumType.STRING) // 🚀 Guarda la palabra del Enum limpia en la base de datos
@Column(name = "nombre_oficina", nullable = false)
private NombreOficina nombreOficina; // <-- Cambiado de String a tu Enum real
    // Piso
    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion_piso", nullable = false)
    private PisoUbicacion ubicacionPiso;

    // Descripción
    @Column(name = "observacion", length = 300)
    private String observacion;

    // Activa
    @Column(nullable = false)
    private Boolean activa = true;

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

        if (activa == null) {
            activa = true;
        }

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //extra
    // Identificador visible
    @Column(length = 30)
    private String referencia;

}