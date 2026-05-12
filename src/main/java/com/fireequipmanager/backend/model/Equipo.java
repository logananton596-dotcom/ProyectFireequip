package com.fireequipmanager.backend.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "equipo", uniqueConstraints = {
        @UniqueConstraint(columnNames = "numeroSerie"),
        @UniqueConstraint(columnNames = "codigoInterno")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoInterno;

    @Column(nullable = false)
    private String numeroSerie;

    @Column(nullable = false)
    private String nombre;

    private String marca;
    private String modelo;

    private LocalDate fechaCompra;

    private Integer vidaUtilAnios;

    private String ubicacionActual;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoEquipo tipoEquipo;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoEquipo estadoEquipo;

    //para dar de baja 
    private String motivoBaja;
    private String autorizadoPor;
    private LocalDate fechaBaja;


    //para seguir un historial
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
