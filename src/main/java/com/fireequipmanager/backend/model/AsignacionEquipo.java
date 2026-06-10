package com.fireequipmanager.backend.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion_equipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIÓN CON EL EQUIPO ---
    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    // --- RELACIÓN CON EL BOMBERO (RECEPTOR) ---
    @ManyToOne
    @JoinColumn(name = "bombero_id", nullable = false)
    private Bombero bombero; // <-- AQUÍ SE CONECTA LA REFORMA

    // --- CARACTERÍSTICAS ESPECÍFICAS DE ESTA ENTREGA ---
    private String tallaEquipo; 
    
    @Column(columnDefinition = "TEXT")
    private String caracteristicasEspecificas; 

    // --- FECHAS CRÍTICAS DE ESTA UNIDAD ---
    private LocalDate fechaPuestaOperatividad;
    private LocalDate fechaCaducidad;

   // --- CAMBIO: Ahora son String puros para recibir el texto del Frontend ---
    @Column(nullable = false, length = 50)
    private String estadoFisicoEntrega; // Ej: "Nuevo", "Bueno", "Usado"

    @Column(nullable = false, length = 50)
    private String tipoMovimiento; // Ej: "Alta por ingreso", "Reemplazo", "Pérdida"


    // --- DATOS DEL RESPONSABLE QUE ENTREGA ---
    @Column(nullable = false)
    private String responsableNombre;
    @Column(nullable = false)
    private String responsableCodigo;
    private String responsableGrado;
    private String responsableCargo;
    private String responsableTelefono;

    // --- AUDITORÍA ---
    @Column(nullable = false)
    private LocalDateTime fechaHoraEntrega;

    @PrePersist
    public void prePersist() {
        this.fechaHoraEntrega = LocalDateTime.now();
    }

}