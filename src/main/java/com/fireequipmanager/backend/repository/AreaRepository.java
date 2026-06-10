package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    // 1. Permite buscar un área por su nombre exacto (útil para validaciones al crear)
    Optional<Area> findByNombre(String nombre);

    // 2. Permite verificar si ya existe un área con ese nombre antes de guardarla
    boolean existsByNombre(String nombre);

    // 3. Busca áreas cuyo nombre contenga una palabra clave (útil para buscadores/filtros)
    List<Area> findByNombreContainingIgnoreCase(String termino);
}