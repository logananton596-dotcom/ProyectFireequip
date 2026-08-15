package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Oficina;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreOficina;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OficinaRepository extends JpaRepository<Oficina, Long> {

    Optional<Oficina> findByNombreOficina(NombreOficina nombreOficina);

    boolean existsByNombreOficina(NombreOficina nombreOficina);

    boolean existsByNombreOficinaAndUbicacionPiso(
        NombreOficina nombreOficina,
        PisoUbicacion pisoUbicacion
    );

    boolean existsByNombreOficinaAndUbicacionPisoAndIdNot(
            NombreOficina nombreOficina,
            PisoUbicacion pisoUbicacion,
            Long id
    );

    List<Oficina> findByActivaTrue();
    List<Oficina> findByUbicacionPiso(PisoUbicacion ubicacionPiso);
}