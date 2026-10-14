package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import com.fireequipmanager.backend.model.enumsEquipo.EstadoEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.NombreEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.TipoEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.TipoInventario;
import com.fireequipmanager.backend.service.contracts.StockEntity;
import com.fireequipmanager.backend.service.contracts.VidaUtilEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "equipo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo_cgbvp"),
                @UniqueConstraint(columnNames = "numero_serie")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo  implements VidaUtilEntity, StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código institucional
    @Column(name = "codigo_cgbvp", unique = true, length = 30)
    private String codigoCgbvp;

    // Número de serie
    @Column(name = "numero_serie", unique = true, length = 50)
    private String numeroSerie;

    // Nombre del equipo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NombreEquipo nombreEquipo;

    // Tipo del equipo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEquipo tipoEquipo;

    // Tipo de inventario
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInventario tipoInventario;

    // Compañía propietaria
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompaniasBombero compania;

    // Fecha de incorporación
    @Column(nullable = false)
    private LocalDate fechaIncorporacion;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipo estado = EstadoEquipo.OPERATIVO;

    // Fecha de baja
    private LocalDate fechaBaja;

    // Motivo de baja
    @Column(length = 300)
    private String motivoBaja;

    // Vida útil
    @Column(nullable = false)
    private Integer vidaUtilAnios;

    // Stock disponible
    @Column(nullable = false)
    private Integer stock = 1;

    // Stock mínimo permitido
    @Column(nullable = false)
    private Integer stockMinimo = 1;

    // Especificación técnica
    @Column(length = 150)
    private String especificacion;

    // Material
    @Column(length = 80)
    private String material;

    // Marca
    @Column(length = 80)
    private String marca;

    // Modelo
    @Column(length = 80)
    private String modelo;

    // Color
    @Column(length = 50)
    private String color;

    // Observaciones
    @Column(length = 500)
    private String observaciones;

    // Indica si existe una asignación activa
    @Column(nullable = false)
    private Boolean asignado = false;

    // Área propietaria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    // Auditoría
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        normalizarCamposUnicos();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
         normalizarCamposUnicos();
    }

    // Descripción administrativa
        @Column(length = 150)
        private String descripcion;

         // RELACIÓN CON UBICACIÓN
    // ==========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    // ==========================
    // RELACIÓN CON CASILLERO (si aplica)
    // ==========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casillero_id")
    private Casillero casillero;

  /**
     * Convierte cualquier cadena vacía o con puros espacios en un null real.
     * Esto evita que la base de datos lance un error de restricción única (Unique Constraint)
     * al registrar múltiples equipos en masa sin código institucional o número de serie.
     */
    private void normalizarCamposUnicos() {
        if (this.codigoCgbvp != null && this.codigoCgbvp.trim().isEmpty()) {
            this.codigoCgbvp = null;
        }
        if (this.numeroSerie != null && this.numeroSerie.trim().isEmpty()) {
            this.numeroSerie = null;
        }
    }
}