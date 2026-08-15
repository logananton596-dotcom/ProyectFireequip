package com.fireequipmanager.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fireequipmanager.backend.model.Bien;
import com.fireequipmanager.backend.model.enumsBien.EstadoBien;
import com.fireequipmanager.backend.model.enumsBien.TipoBien;

@Repository
public interface BienRepository extends JpaRepository<Bien, Long> {

    // Busca un bien por código CGBVP
    Optional<Bien> findByCodigoCgbvp(String codigoCgbvp);

    // Valida si existe un código CGBVP
    boolean existsByCodigoCgbvp(String codigoCgbvp);

    // Valida código CGBVP al actualizar
    boolean existsByCodigoCgbvpAndIdNot(String codigoCgbvp, Long id);

    // Lista bienes activos
    List<Bien> findByActivoTrue();

    // Lista bienes por estado
    List<Bien> findByEstado(EstadoBien estado);

    // Lista bienes por tipo
    List<Bien> findByTipoBien(TipoBien tipoBien);

    // Lista bienes por estado y activos
    List<Bien> findByEstadoAndActivoTrue(EstadoBien estado);

    // Lista bienes asociados a una ubicación
    List<Bien> findByUbicacionId(Long ubicacionId);
}