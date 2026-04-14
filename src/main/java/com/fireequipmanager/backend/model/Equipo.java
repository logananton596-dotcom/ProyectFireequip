package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "equipo", uniqueConstraints = {
        @UniqueConstraint(columnNames = "numeroSerie"),
        @UniqueConstraint(columnNames = "codigoInterno")
})
@Data
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

    private Integer vidaUtilMeses;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoEquipo tipo;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoEquipo estado;
}
