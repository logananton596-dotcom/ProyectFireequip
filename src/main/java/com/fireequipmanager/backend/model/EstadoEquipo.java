package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estado_equipo")
@Data
public class EstadoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
