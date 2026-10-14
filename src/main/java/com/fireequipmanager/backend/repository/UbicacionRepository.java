package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.EstadoUbicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    // Busca todas las ubicaciones activas
    List<Ubicacion> findByActivaTrue();

    // Busca por tipo
    Optional<Ubicacion> findByNombreUbicacion(NombreUbicacion nombreUbicacion);

    // Busca ubicaciones activas por tipo
    List<Ubicacion> findByNombreUbicacionAndActivaTrue(
            NombreUbicacion nombreUbicacion);

    // Comprueba si ya existe una ubicación con ese Enum al crear
    boolean existsByNombreUbicacion(NombreUbicacion nombreUbicacion);

    // Comprueba si el Enum ya pertenece a otra ubicación diferente al actualizar
    boolean existsByNombreUbicacionAndIdNot(NombreUbicacion nombreUbicacion, Long id);

    Optional<Ubicacion> findByCodigo(String codigo);
    // Cambia List<Ubicacion> por Optional<Ubicacion>

    List<Ubicacion> findByEstado(EstadoUbicacion estado
    );

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo,Long id);

}