
package com.fireequipmanager.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
// Anotaciones de Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "area", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String encargado;

    @Column(length = 20)
    private String telefono;

    // Relación bidireccional opcional (útil si necesitas listar equipos desde el área)
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<Equipo> equipos;
}