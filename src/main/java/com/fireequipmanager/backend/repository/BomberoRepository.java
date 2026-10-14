package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BomberoRepository extends JpaRepository<Bombero, Long> {

    // Lista bomberos por estado
    List<Bombero> findByEstado(EstadoBombero estado);

    // Busca por código CGBVP
    Optional<Bombero> findByCodigoCgbvp(String codigoCgbvp);

    // Busca por DNI
    Optional<Bombero> findByDni(String dni);

    // Valida código CGBVP duplicado
    boolean existsByCodigoCgbvp(String codigoCgbvp);

    // Valida código CGBVP al actualizar
    boolean existsByCodigoCgbvpAndIdNot(String codigoCgbvp, Long id);

    // Valida DNI duplicado
    boolean existsByDni(String dni);

    // Valida DNI al actualizar
    boolean existsByDniAndIdNot(String dni, Long id);

    // Busca por nombre y estado
    List<Bombero> findByNombreContainingIgnoreCaseAndEstado(
            String nombre,
            EstadoBombero estado
    );

}