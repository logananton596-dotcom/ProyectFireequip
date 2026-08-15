package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsEpp.*;
import com.fireequipmanager.backend.service.contracts.VidaUtilEntity;
import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
        name = "epp",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo_cgbvp"),
                @UniqueConstraint(columnNames = "numero_serie")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Epp  implements VidaUtilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código patrimonial CGBVP
    @Column(name = "codigo_cgbvp", nullable = false, unique = true, length = 30)
    private String codigoCgbvp;

    // Número de serie
    @Column(name = "numero_serie", nullable = false, unique = true, length = 50)
    private String numeroSerie;

    // Nombre del EPP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NombreEpp nombreEpp;

    // Tipo del EPP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEpp tipoEpp;

    // Compañía propietaria
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompaniasBombero compania;

    // Fecha de incorporación
    @Column(nullable = false)
    private LocalDate fechaIncorporacion;

    // Estado del EPP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEpp estado = EstadoEpp.OPERATIVO;

    // Fecha de baja
    private LocalDate fechaBaja;

    // Motivo de baja
    @Column(length = 300)
    private String motivoBaja;

    // Vida útil (años)
    @Column(nullable = false)
    private Integer vidaUtilAnios;

    // Talla
    @Column(length = 20)
    private String talla;

    // Material
    @Column(length = 80)
    private String material;

    // Marca
    @Column(length = 80)
    private String marca;

    // Color
    @Column(length = 50)
    private String color;

    // Observaciones
    @Column(length = 500)
    private String observaciones;

    // Indica si actualmente está asignado
    @Column(nullable = false)
    private Boolean asignado = false;

    // Activo lógico del registro
    @Column(nullable = false)
    private Boolean activo = true;

    // Área propietaria del EPP
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    // Fecha de creación
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // Fecha de última actualización
    private LocalDateTime updatedAt;

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

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
      //  Asignación limpia utilizando el Enum correcto EstadoEpp
        if (asignado == null) {
            asignado = false;
        }
        if (estado == null) {
            estado = EstadoEpp.OPERATIVO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Fecha calculada del fin de vida útil
    @Transient
    public LocalDate getFechaFinVidaUtil() {
        if (fechaIncorporacion == null || vidaUtilAnios == null) {
            return null;
        }
        return fechaIncorporacion.plusYears(vidaUtilAnios);
    }
}