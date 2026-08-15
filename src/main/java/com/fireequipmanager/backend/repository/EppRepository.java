package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Epp;
import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import com.fireequipmanager.backend.model.enumsEpp.EstadoEpp;
import com.fireequipmanager.backend.model.enumsEpp.NombreEpp;
import com.fireequipmanager.backend.model.enumsEpp.TipoEpp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EppRepository extends JpaRepository<Epp, Long> {
    // VALIDACIONES

    // Verifica si el código CGBVP ya existe
    boolean existsByCodigoCgbvp(String codigoCgbvp);

    // Verifica si el código CGBVP pertenece a otro registro
    boolean existsByCodigoCgbvpAndIdNot(String codigoCgbvp, Long id);

    // Verifica si el número de serie ya existe
    boolean existsByNumeroSerie(String numeroSerie);

    // Verifica si el número de serie pertenece a otro registro
    boolean existsByNumeroSerieAndIdNot(String numeroSerie, Long id);

    // BÚSQUEDAS

    // Buscar por código institucional
    Optional<Epp> findByCodigoCgbvp(String codigoCgbvp);

    // Buscar por número de serie
    Optional<Epp> findByNumeroSerie(String numeroSerie);
    @SuppressWarnings("null") 
    Optional<Epp> findById(Long id);
    // FILTROS

    // Filtrar por estado
    List<Epp> findByEstado(EstadoEpp estado);

    // Filtrar por tipo
    List<Epp> findByTipoEpp(TipoEpp tipoEpp);

    // Filtrar por nombre
    List<Epp> findByNombreEpp(NombreEpp nombreEpp);

    // Filtrar por compañía
    List<Epp> findByCompania(CompaniasBombero compania);

    // Filtrar por área
    List<Epp> findByArea(Area area);

    // Filtrar por asignación
    List<Epp> findByAsignado(Boolean asignado);
    
    // FILTROS COMPUESTOS

    List<Epp> findByNombreEppAndEstado(
            NombreEpp nombreEpp,
            EstadoEpp estado
    );

    List<Epp> findByAreaAndEstado(
            Area area,
            EstadoEpp estado
    );

    List<Epp> findByCompaniaAndEstado(
            CompaniasBombero compania,
            EstadoEpp estado
    );

    List<Epp> findByAsignadoAndEstado(
            Boolean asignado,
            EstadoEpp estado
    );

    List<Epp> findByAreaAndAsignado(
            Area area,
            Boolean asignado
    );

}

/*Consultas que agregaremos en futuros módulos

Cuando implementemos Asignaciones, Mantenimientos y Movimientos, añadiremos consultas más específicas como:

List<Epp> findByEstadoAndAsignado(...);

List<Epp> findByAreaAndAsignado(...);

List<Epp> findByEstadoAndArea(...);

List<Epp> findByEstadoAndTipoEpp(...);

List<Epp> findByAsignadoFalseAndEstado(...);

List<Epp> findByAsignadoTrue(...);*/