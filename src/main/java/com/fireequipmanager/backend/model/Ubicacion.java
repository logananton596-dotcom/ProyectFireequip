package com.fireequipmanager.backend.model;

import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ubicacion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único ("P1", "P1-OF-LOG", "P1-OF-LOG-C03")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    // Nombre visible ("Piso 1", "Oficina Logística", "Casillero A-03")
    @Column(nullable = false, length = 120)
    private String nombre;

    // Tipo de ubicación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoUbicacion tipo;

    // Solo se usa cuando tipo = OTRO
    @Column(name = "tipo_personalizado", length = 50)
    private String tipoPersonalizado;

    // Jerarquía: ubicación padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Ubicacion padre;

    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY)
    private List<Ubicacion> hijos = new ArrayList<>();

    // Área propietaria (opcional; solo oficinas, depósitos, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    // Activo lógico
    @Column(nullable = false)
    private Boolean activa = true;

    // Observaciones
    @Column(length = 300)
    private String observacion;

    // Auditoría
    @Column(updatable = false, nullable = false)
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
}