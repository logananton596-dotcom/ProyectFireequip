package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    // Búsquedas básicas
    Optional<Ubicacion> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    // Filtros
    List<Ubicacion> findByTipo(TipoUbicacion tipo);

    List<Ubicacion> findByActivaTrue();

    List<Ubicacion> findByPadreId(Long padreId);

    List<Ubicacion> findByPadreIsNull();

    List<Ubicacion> findByAreaId(Long areaId);

    // Validación: ¿la ubicación tiene hijos?
    boolean existsByPadreId(Long padreId);

    // Validación: ¿la ubicación está referenciada por equipos, epp o bienes?
    @Query("SELECT COUNT(e) > 0 FROM Equipo e WHERE e.ubicacion.id = :ubicacionId")
    boolean existsEquipoEnUbicacion(@Param("ubicacionId") Long ubicacionId);

    @Query("SELECT COUNT(e) > 0 FROM Epp e WHERE e.ubicacion.id = :ubicacionId")
    boolean existsEppEnUbicacion(@Param("ubicacionId") Long ubicacionId);

    @Query("SELECT COUNT(b) > 0 FROM Bien b WHERE b.ubicacion.id = :ubicacionId")
    boolean existsBienEnUbicacion(@Param("ubicacionId") Long ubicacionId);
}