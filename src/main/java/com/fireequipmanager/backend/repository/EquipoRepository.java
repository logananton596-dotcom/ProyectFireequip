package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.enumsBombero.CompaniasBombero;
import com.fireequipmanager.backend.model.enumsEquipo.EstadoEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.NombreEquipo;
import com.fireequipmanager.backend.model.enumsEquipo.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // VALIDACIONES

    // Valida código CGBVP único
    boolean existsByCodigoCgbvp(String codigoCgbvp);
    boolean existsByCodigoCgbvpAndIdNot(
            String codigoCgbvp,
            Long id
    );

    // Valida número de serie único
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByNumeroSerieAndIdNot(
            String numeroSerie,
            Long id
    );

    // BÚSQUEDAS

    Optional<Equipo> findByCodigoCgbvp(String codigoCgbvp);

    Optional<Equipo> findByNumeroSerie(String numeroSerie);

    // FILTROS

    // Estado
    List<Equipo> findByEstado(EstadoEquipo estado);

    // Tipo
    List<Equipo> findByTipoEquipo(TipoEquipo tipoEquipo);

    // Nombre
    List<Equipo> findByNombreEquipo(NombreEquipo nombreEquipo);

    // Área
    List<Equipo> findByArea(Area area);

    // Compañía
    List<Equipo> findByCompania(CompaniasBombero compania);

    // Asignado
    List<Equipo> findByAsignado(Boolean asignado);
        
    // Cantidad mayor a 1
    List<Equipo> findByStockGreaterThan(Integer stock);

  // ==========================================
    // MÉTODOS SUSPENDIDOS (PROPIEDADES INEXISTENTES EN EL SERVICE)
    // ==========================================
    // Si vuelves a agregar estas variables a tu Entidad/Service, quita las barras "//"
    
    // List<Equipo> findByTieneNumeroSerie(Boolean tieneNumeroSerie);
    // List<Equipo> findByControlIndividual(Boolean controlIndividual);

    // FILTROS COMPUESTOS
   
    List<Equipo> findByEstadoAndArea(
            EstadoEquipo estado,
            Area area
    );

    List<Equipo> findByNombreEquipoAndEstado(
            NombreEquipo nombreEquipo,
            EstadoEquipo estado
    );

    List<Equipo> findByTipoEquipoAndEstado(
            TipoEquipo tipoEquipo,
            EstadoEquipo estado
    );

    List<Equipo> findByAreaAndEstado(
            Area area,
            EstadoEquipo estado
    );

    List<Equipo> findByCompaniaAndEstado(
            CompaniasBombero compania,
            EstadoEquipo estado
    );

}