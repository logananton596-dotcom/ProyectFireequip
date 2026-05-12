package com.fireequipmanager.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fireequipmanager.backend.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByNumeroSerieAndIdNot(String numeroSerie, Long id);
    boolean existsByCodigoInterno(String codigoInterno);

    
    @Query("SELECT e.estadoEquipo.nombre, COUNT(e) FROM Equipo e GROUP BY e.estadoEquipo.nombre")
    List<Object[]> countByEstado();
}
