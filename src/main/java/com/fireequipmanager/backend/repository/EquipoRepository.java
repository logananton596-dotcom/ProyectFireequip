package com.fireequipmanager.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByNumeroSerieAndIdNot(String numeroSerie, Long id);
    boolean existsByCodigoInterno(String codigoInterno);
}
