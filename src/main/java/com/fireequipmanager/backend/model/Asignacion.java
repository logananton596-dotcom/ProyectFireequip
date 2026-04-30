package com.fireequipmanager.backend.model;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// Anotaciones de Lombok
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter // Genera automáticamente todos los getters
@Setter // Genera automáticamente todos los setters
@NoArgsConstructor // Genera constructor vacío (obligatorio para JPA)
@AllArgsConstructor // Genera constructor con todos los campos (opcional pero útil)
@Entity
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con equipo
    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    // Tipo de asignación: ESTACION, VEHICULO, BOMBERO
    private String tipoAsignacion;

    // Identificador del destino (nombre o ID)
    private String destino;

    // Fecha inicio
    private LocalDateTime fechaInicio;

    // Fecha fin (null = asignación activa)
    private LocalDateTime fechaFin;

}