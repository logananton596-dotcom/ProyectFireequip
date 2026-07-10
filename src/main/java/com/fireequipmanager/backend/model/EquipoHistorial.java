package com.fireequipmanager.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Historial_Equipo")
public class EquipoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;

    private LocalDateTime fechaCambio;

    private String usuario;

    @ManyToOne
    private Equipo equipo;

      //para dar de baja 
    private String motivoBaja;
    private String autorizadoPor;
    private LocalDate fechaBaja;

}