package com.fireequipmanager.backend.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "bombero", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bombero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String codigo; // Ej: "B-205" o DNI

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String grado; // Ej: Seccionario, Teniente, Capitán

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true; // Control de bomberos activos/inactivos

    // Relación inversa opcional por si quieres ver qué tiene asignado un bombero desde su perfil
    @OneToMany(mappedBy = "bombero")
    private List<AsignacionEquipo> asignaciones;
}