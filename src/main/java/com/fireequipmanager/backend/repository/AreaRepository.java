package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.enumsArea.NombreArea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    // Busca un área por nombre
    Optional<Area> findByNombreArea(NombreArea nombreArea);

    // Valida nombre duplicado
    boolean existsByNombreArea(NombreArea nombreArea);

    boolean existsByEncargado1OrEncargado2(Bombero encargado1, Bombero encargado2);
    boolean existsByEncargado1Id(Long id);
    boolean existsByEncargado1IdAndIdNot(Long encargadoId, Long areaId);

   // Verifica si un bombero ya es encargado (principal o secundario) en alguna área activa
     
    @Query("SELECT COUNT(a) > 0 FROM Area a WHERE a.activo = true " +
           "AND (a.encargado1.id = :bomberoId OR a.encargado2.id = :bomberoId) " +
           "AND a.id != :idIgnorar")
    boolean existsBomberoEnAlgunaArea(@Param("bomberoId") Long bomberoId, 
                                       @Param("idIgnorar") Long idIgnorar);

    //Verifica si un bombero ya es encargado principal en alguna área activa
    @Query("SELECT COUNT(a) > 0 FROM Area a WHERE a.activo = true " +
           "AND a.encargado1.id = :bomberoId " +
           "AND a.id != :idIgnorar")
    boolean existsBomberoComoPrincipalEnArea(@Param("bomberoId") Long bomberoId, 
                                              @Param("idIgnorar") Long idIgnorar);

    //Verifica si un bombero ya es encargado secundario en alguna área activa
     
    @Query("SELECT COUNT(a) > 0 FROM Area a WHERE a.activo = true " +
           "AND a.encargado2.id = :bomberoId " +
           "AND a.id != :idIgnorar")
    boolean existsBomberoComoSecundarioEnArea(@Param("bomberoId") Long bomberoId, 
                                               @Param("idIgnorar") Long idIgnorar);

}