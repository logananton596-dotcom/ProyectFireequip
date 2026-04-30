package com.fireequipmanager.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

// 1. Importaciones de Lombok
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter // Genera automáticamente getEquipo(), getFecha(), etc.
@Setter // Genera automáticamente setEquipo(...), setFecha(...), etc.
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Equipo (muchos mantenimientos pertenecen a un equipo)
    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    // Fecha en la que se realizó el mantenimiento
    private LocalDate fecha;

    // Tipo: PREVENTIVO o CORRECTIVO
    private String tipo;

    // Descripción de lo que se hizo
    private String descripcion;

    // Persona responsable del mantenimiento
    private String responsable;

    // Fecha del próximo mantenimiento (para alertas)
    private LocalDate fechaProximo;
    // Fecha del fin de mantenimiento
    private LocalDate fechaFin;
}