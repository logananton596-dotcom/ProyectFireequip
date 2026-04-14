package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipo_equipo")
@Data
public class TipoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;
}
