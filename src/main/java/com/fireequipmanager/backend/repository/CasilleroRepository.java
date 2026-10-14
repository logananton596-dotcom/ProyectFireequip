package com.fireequipmanager.backend.repository;


import com.fireequipmanager.backend.model.Casillero;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface CasilleroRepository extends JpaRepository<Casillero, Long> {

  // Busca por número de casillero
    Optional<Casillero> findByNumIdentificadorCasillero(Integer numIdentificadorCasillero);

    // Valida si ya existe el número
    boolean existsByNumIdentificadorCasillero(Integer numIdentificadorCasillero);

    // Valida duplicados en edición
    boolean existsByNumIdentificadorCasilleroAndIdNot(Integer numIdentificadorCasillero, Long id);

    // 🚀 REFACTORIZADO: Si activo es false, significa que el casillero está deshabilitado
    List<Casillero> findByActivoFalse();

    // 🚀 REFACTORIZADO: Para listar casilleros OCUPADOS, filtramos por tu propiedad 'ocupado' en true
    List<Casillero> findByOcupadoTrue();

    // 🚀 REFACTORIZADO: Para listar casilleros libres/no asignados, usamos tu propiedad 'asignado' en false
    List<Casillero> findByAsignadoFalse();
    List<Casillero> findByActivoTrue();

    // Agrega esta línea dentro de tu OficinaRepository.java
    List<Casillero> findByPisoUbicacion(PisoUbicacion ubicacionPiso);

    // 🚀 REFACTORIZADO: Para listar casilleros por piso, usamos tu propiedad 'pisoUbicacion'
    //Collection<CasilleroDTO> findByPisoUbicacion(PisoUbicacion pisoEnum);

}
