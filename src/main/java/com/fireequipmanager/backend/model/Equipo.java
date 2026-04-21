package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

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
}
