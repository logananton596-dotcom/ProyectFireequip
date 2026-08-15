package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.CompartimientoVehiculo;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoCompartimiento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompartimientoVehiculoRepository
        extends JpaRepository<CompartimientoVehiculo, Long> {
    // Busca compartimientos de un vehículo
    List<CompartimientoVehiculo> findByVehiculoId(Long vehiculoId);

    // REFACTORIZADO: Cambiado de NombreCompartimiento a TipoCompartimiento
    Optional<CompartimientoVehiculo> findByVehiculoIdAndTipoCompartimiento(
            Long vehiculoId,
            TipoCompartimiento tipoCompartimiento);

    // REFACTORIZADO: Valida tipos repetidos dentro del mismo vehículo
    boolean existsByVehiculoIdAndTipoCompartimiento(
            Long vehiculoId,
            TipoCompartimiento tipoCompartimiento);

    // REFACTORIZADO: Valida duplicados al actualizar
    boolean existsByVehiculoIdAndTipoCompartimientoAndIdNot(
            Long vehiculoId,
            TipoCompartimiento tipoCompartimiento,
            Long id);

    // Lista compartimientos activos de un vehículo
    List<CompartimientoVehiculo> findByVehiculoIdAndActivoTrue(Long vehiculoId);
// Agrega esta línea dentro de tu CompartimientoVehiculoRepository.java si no la tienes
List<CompartimientoVehiculo> findByActivoTrue();

}