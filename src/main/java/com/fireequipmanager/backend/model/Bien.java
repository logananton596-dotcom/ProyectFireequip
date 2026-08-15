package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsArea.NombreArea;
import com.fireequipmanager.backend.model.enumsBien.EstadoBien;
import com.fireequipmanager.backend.model.enumsBien.TipoBien;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bienes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo_inventario")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código interno o patrimonial del bien
    @Column(
            name = "codigo_inventario",
            nullable = false,
            unique = true,
            length = 30
    )
    private String codigoCgbvp;

    // Nombre general del bien
    @Column(
            nullable = false,
            length = 100
    )
    private String nombre;

    // Tipo de bien
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private TipoBien tipoBien;

    // Marca del bien
    @Column(length = 80)
    private String marca;

    // Modelo del bien
    @Column(length = 80)
    private String modelo;

    // Número de serie cuando el bien lo tenga
    @Column(
            name = "numero_serie",
            length = 80
    )
    private String numeroSerie;

    // Fecha de adquisición o incorporación
    @Column(nullable = false)
    private LocalDate fechaAdquisicion;

    // Estado operativo del bien
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private EstadoBien estado = EstadoBien.OPERATIVO;

    // Condición física general
    @Column(length = 50)
    private String condicion;

    // Valor referencial del bien
    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal valorReferencial;

    // Especificaciones particulares del bien
    @Column(length = 1000)
    private String especificaciones;

    // Observaciones generales
    @Column(length = 500)
    private String observaciones;

    // Área propietaria del bien
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private NombreArea area;

    // Ubicación física actual del bien
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ubicacion_id",
            nullable = false
    )
    private Ubicacion ubicacion;

    // Activo lógico del registro
    @Column(nullable = false)
    private Boolean activo = true;

    // Fecha de creación
    @Column(
            updatable = false,
            nullable = false
    )
    private LocalDateTime createdAt;

    // Fecha de última actualización
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoBien.OPERATIVO;
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