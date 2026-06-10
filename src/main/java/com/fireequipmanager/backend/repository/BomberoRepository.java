package com.fireequipmanager.backend.repository;


import com.fireequipmanager.backend.model.Bombero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BomberoRepository extends JpaRepository<Bombero, Long> {

    // 1. Clave para el formulario de asignación: Lista solo bomberos habilitados/activos
    List<Bombero> findByActivoTrue();

    // 2. Permite buscar un bombero por su código único (DNI o placa)
    Optional<Bombero> findByCodigo(String codigo);

    // 3. Regla de Negocio: Validar si ya existe el código antes de registrar un alta
    boolean existsByCodigo(String codigo);

    // 4. Regla de Negocio: Validar duplicados de código al actualizar los datos de un bombero
    boolean existsByCodigoAndIdNot(String codigo, Long id);

    // 5. Utilidad extra: Buscar bomberos por nombre (para buscadores dinámicos en el frontend)
    List<Bombero> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}