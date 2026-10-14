package com.fireequipmanager.backend.repository;

import com.fireequipmanager.backend.model.Vehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.TipoVehiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // Busca un vehículo por placa
    Optional<Vehiculo> findByPlaca(String placa);

    // Valida si la placa ya existe
    boolean existsByPlaca(String placa);

    // Valida duplicados al actualizar
    boolean existsByPlacaAndIdNot(String placa, Long id);

    // Filtra vehículos por estado
    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    // Filtra vehículos por tipo
    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo);

    // Lista vehículos disponibles para asignación
    List<Vehiculo> findByActivoTrue();
    List<Vehiculo> findByEstadoAndActivoTrue(EstadoVehiculo estado);
}