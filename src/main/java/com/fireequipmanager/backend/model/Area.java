
package com.fireequipmanager.backend.model;

// Anotaciones de Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import com.fireequipmanager.backend.model.enumsArea.NombreArea;
@Entity
@Table(
    name = "area",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre_area")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_area", nullable = false, unique = true, length = 50)
    private NombreArea nombreArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado1_id", nullable = false)
    private Bombero encargado1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado2_id")
    private Bombero encargado2;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Column(length = 300)
    private String observaciones;

    private Boolean activo = true;


    // Relación bidireccional opcional (útil si necesitas listar equipos desde el área)
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<Equipo> equipos;
}